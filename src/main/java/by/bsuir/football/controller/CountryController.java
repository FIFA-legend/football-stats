package by.bsuir.football.controller;

import by.bsuir.football.dto.country.CreateCountryDto;
import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.country.UpdateCountryDto;
import by.bsuir.football.entity.enums.Continent;
import by.bsuir.football.service.CountryService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryCodeException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CountryController {

    private final int COUNTRIES_COUNT = 10;

    private final String COUNTRY_RETURN_URL = "/queue/countries/return";

    private final CountryService countryService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public CountryController(CountryService countryService, SimpMessagingTemplate messaging) {
        this.countryService = countryService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @ModelAttribute("continents")
    public Continent[] continents() {
        return Continent.values();
    }

    @GetMapping("/countries")
    public String getCountriesPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "countries_page";
    }

    @GetMapping("/country/registration")
    public String getCountryRegistrationPage(Model model) {
        model.addAttribute("emptyCountry", new CreateCountryDto());
        return "country_registration";
    }

    @PostMapping("/country/registration")
    public String registerCountry(@Valid CreateCountryDto createCountryDto, Errors errors, Model model) {
        model.addAttribute("emptyCountry", new CreateCountryDto());
        if (errors.hasErrors()) return "country_registration";
        try {
            countryService.save(createCountryDto);
        } catch (DuplicateCountryNameException e) {
            model.addAttribute("duplicate_country_name_error", "Country name " +
                    createCountryDto.getName() + " is registered already");
            return "country_registration";
        } catch (DuplicateCountryCodeException e) {
            model.addAttribute("duplicate_country_code_error", "Country code " +
                    createCountryDto.getCode() + " is registered already");
            return "country_registration";
        }

        return "redirect:/countries";
    }

    @GetMapping("/country/update/{id}")
    public String getCountryUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateCountryDtoToModel(id, model);
        return "country_update";
    }

    @PostMapping("/country/update/{id}")
    public String updateCountry(@PathVariable Integer id, @Valid UpdateCountryDto updateCountryDto, Errors errors, Model model) {
        updateCountryDto.setId(id);
        putUpdateCountryDtoToModel(id, model);
        if (errors.hasErrors()) return "country_update";
        try {
            countryService.update(updateCountryDto);
        } catch (DuplicateCountryNameException e) {
            model.addAttribute("duplicate_country_name_error", "Country name " +
                    updateCountryDto.getName() + " is registered already");
            return "country_update";
        } catch (DuplicateCountryCodeException e) {
            model.addAttribute("duplicate_country_code_error", "Country code " +
                    updateCountryDto.getCode() + " is registered already");
            return "country_update";
        } catch (CountryNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/countries";
    }

    @MessageMapping("/countries/start")
    public void sendCountriesOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetCountryDto> countries = countryService.getByPage(page, COUNTRIES_COUNT);
        messaging.convertAndSendToUser(username, COUNTRY_RETURN_URL, convertListToArray(countries));
    }

    @MessageMapping("/countries/next")
    public void sendCountriesOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetCountryDto> countries = countryService.getByPage(page, COUNTRIES_COUNT);
        if (countries.isEmpty()) {
            page = page - 1;
            countries = countryService.getByPage(page, COUNTRIES_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, COUNTRY_RETURN_URL, convertListToArray(countries));
    }

    @MessageMapping("/countries/previous")
    public void sendCountriesOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetCountryDto> countries = countryService.getByPage(page, COUNTRIES_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, COUNTRY_RETURN_URL, convertListToArray(countries));
    }

    @MessageMapping("/countries/delete/{id}")
    public void deleteCountry(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        countryService.delete(id);
        List<GetCountryDto> countries = countryService.getByPage(page, COUNTRIES_COUNT);
        messaging.convertAndSendToUser(username, COUNTRY_RETURN_URL, convertListToArray(countries));
    }

    private void putUpdateCountryDtoToModel(Integer id, Model model) {
        try {
            GetCountryDto getCountryDto = countryService.getById(id);
            UpdateCountryDto updateCountryDto = new UpdateCountryDto(
                    getCountryDto.getId(),
                    getCountryDto.getName(),
                    getCountryDto.getCode(),
                    getCountryDto.getContinent()
            );
            model.addAttribute("updateCountryDto", updateCountryDto);
        } catch (CountryNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetCountryDto[] convertListToArray(List<GetCountryDto> list) {
        GetCountryDto[] array = new GetCountryDto[list.size()];
        return list.toArray(array);
    }

}
