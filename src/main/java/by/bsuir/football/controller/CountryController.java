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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @ModelAttribute("continents")
    public Continent[] continents() {
        return Continent.values();
    }

    @GetMapping("/country/registration")
    public String getCountryRegistrationPage(Model model) {
        model.addAttribute("emptyCountry", new CreateCountryDto());
        return "country_registration";
    }

    @PostMapping("/country/registration")
    public String registerCountry(@Valid CreateCountryDto createCountryDto, Errors errors, Model model) {
        if (errors.hasErrors()) return "country_registration";
        try {
            countryService.save(createCountryDto);
        } catch (DuplicateCountryCodeException e) {
            model.addAttribute("duplicate_country_name_error", "Country name " +
                    createCountryDto.getName() + " is registered already");
            return "country_registration";
        } catch (DuplicateCountryNameException e) {
            model.addAttribute("duplicate_country_code_error", "Country code " +
                    createCountryDto.getCode() + " is registered already");
            return "country_registration";
        }

        return "redirect:/countries";
    }

    @GetMapping("/country/update/{id}")
    public String getCountryUpdatePage(@PathVariable Integer id, Model model) {
        try {
            GetCountryDto getCountryDto = countryService.getById(id);
            UpdateCountryDto updateCountryDto = new UpdateCountryDto(
                    getCountryDto.getId(),
                    getCountryDto.getName(),
                    getCountryDto.getCode(),
                    getCountryDto.getContinent()
            );
            model.addAttribute("updateCountryDto", updateCountryDto);
            return "country_update";
        } catch (CountryNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/country/update/{id}")
    public String updateCountry(@PathVariable Integer id, @Valid UpdateCountryDto updateCountryDto, Errors errors, Model model) {
        updateCountryDto.setId(id);
        if (errors.hasErrors()) return "country_update";
        try {
            countryService.update(updateCountryDto);
        } catch (DuplicateCountryCodeException e) {
            model.addAttribute("duplicate_country_name_error", "Country name " +
                    updateCountryDto.getName() + " is registered already");
            return "country_update";
        } catch (DuplicateCountryNameException e) {
            model.addAttribute("duplicate_country_code_error", "Country code " +
                    updateCountryDto.getCode() + " is registered already");
            return "country_update";
        } catch (CountryNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/countries";
    }

}
