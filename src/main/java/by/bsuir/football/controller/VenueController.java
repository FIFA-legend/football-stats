package by.bsuir.football.controller;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.venue.CreateVenueDto;
import by.bsuir.football.dto.venue.GetVenueDto;
import by.bsuir.football.dto.venue.UpdateVenueDto;
import by.bsuir.football.service.CountryService;
import by.bsuir.football.service.VenueService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.venue.DuplicateVenueNameException;
import by.bsuir.football.service.exceptions.venue.VenueNotFoundException;
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
public class VenueController {

    private final int VENUES_COUNT = 10;

    private final String VENUE_RETURN_URL = "/queue/venues/return";

    private final CountryService countryService;

    private final VenueService venueService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public VenueController(CountryService countryService, VenueService venueService, SimpMessagingTemplate messaging) {
        this.countryService = countryService;
        this.venueService = venueService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @ModelAttribute("countries")
    public GetCountryDto[] countries() {
        List<GetCountryDto> countries = countryService.getAll();
        GetCountryDto[] array = new GetCountryDto[countries.size()];
        return countries.toArray(array);
    }

    @GetMapping("/venues")
    public String getVenuesPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "venues_page";
    }

    @GetMapping("/venue/registration")
    public String getVenueRegistrationPage(Model model) {
        model.addAttribute("emptyVenue", new CreateVenueDto());
        return "venue_registration";
    }

    @PostMapping("/venue/registration")
    public String registerVenue(@Valid CreateVenueDto createVenueDto, Errors errors, Model model) {
        model.addAttribute("emptyVenue", new CreateVenueDto());
        if (errors.hasErrors()) return "venue_registration";
        try {
            venueService.save(createVenueDto);
        } catch (DuplicateVenueNameException e) {
            model.addAttribute("duplicate_venue_name_error", "Venue name " +
                    createVenueDto.getName() + " is registered already");
            return "venue_registration";
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    createVenueDto.getCountryId() + " is not found");
            return "venue_registration";
        }

        return "redirect:/venues";
    }

    @GetMapping("/venue/update/{id}")
    public String getVenueUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateVenueDtoToModel(id, model);
        return "venue_update";
    }

    @PostMapping("/venue/update/{id}")
    public String updateVenue(@PathVariable Integer id, @Valid UpdateVenueDto updateVenueDto, Errors errors, Model model) {
        updateVenueDto.setId(id);
        putUpdateVenueDtoToModel(id, model);
        if (errors.hasErrors()) return "venue_update";
        try {
            venueService.update(updateVenueDto);
        } catch (DuplicateVenueNameException e) {
            model.addAttribute("duplicate_venue_name_error", "Venue name " +
                    updateVenueDto.getName() + " is registered already");
            return "venue_update";
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    updateVenueDto.getCountryId() + " is not found");
            return "venue_update";
        } catch (VenueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "redirect:/venues";
    }

    @MessageMapping("/venues/start")
    public void sendVenuesOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetVenueDto> venues = venueService.getByPage(page, VENUES_COUNT);
        messaging.convertAndSendToUser(username, VENUE_RETURN_URL, convertListToArray(venues));
    }

    @MessageMapping("/venues/next")
    public void sendVenuesOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetVenueDto> venues = venueService.getByPage(page, VENUES_COUNT);
        if (venues.isEmpty()) {
            page = page - 1;
            venues = venueService.getByPage(page, VENUES_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, VENUE_RETURN_URL, convertListToArray(venues));
    }

    @MessageMapping("/venues/previous")
    public void sendVenuesOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetVenueDto> venues = venueService.getByPage(page, VENUES_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, VENUE_RETURN_URL, convertListToArray(venues));
    }

    @MessageMapping("/venues/delete/{id}")
    public void deleteVenue(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        venueService.delete(id);
        List<GetVenueDto> venues = venueService.getByPage(page, VENUES_COUNT);
        messaging.convertAndSendToUser(username, VENUE_RETURN_URL, convertListToArray(venues));
    }

    private void putUpdateVenueDtoToModel(Integer id, Model model) {
        try {
            GetVenueDto getVenueDto = venueService.getById(id);
            UpdateVenueDto updateVenueDto = new UpdateVenueDto(
                    getVenueDto.getId(),
                    getVenueDto.getName(),
                    getVenueDto.getCapacity(),
                    getVenueDto.getCity(),
                    getVenueDto.getCountry().getId()
            );
            model.addAttribute("updateVenueDto", updateVenueDto);
        } catch (VenueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetVenueDto[] convertListToArray(List<GetVenueDto> list) {
        GetVenueDto[] array = new GetVenueDto[list.size()];
        return list.toArray(array);
    }

}
