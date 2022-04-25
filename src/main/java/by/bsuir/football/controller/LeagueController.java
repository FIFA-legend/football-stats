package by.bsuir.football.controller;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.league.CreateLeagueDto;
import by.bsuir.football.dto.league.GetLeagueDto;
import by.bsuir.football.dto.league.UpdateLeagueDto;
import by.bsuir.football.service.CountryService;
import by.bsuir.football.service.LeagueService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.league.DuplicateLeagueNameException;
import by.bsuir.football.service.exceptions.league.LeagueNotFoundException;
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
public class LeagueController {

    private final int LEAGUES_COUNT = 10;

    private final String LEAGUE_RETURN_URL = "/queue/leagues/return";

    private final CountryService countryService;

    private final LeagueService leagueService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public LeagueController(CountryService countryService, LeagueService leagueService, SimpMessagingTemplate messaging) {
        this.countryService = countryService;
        this.leagueService = leagueService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @ModelAttribute("countries")
    public GetCountryDto[] countries() {
        List<GetCountryDto> countries = countryService.getAll();
        GetCountryDto[] array = new GetCountryDto[countries.size()];
        return countries.toArray(array);
    }

    @GetMapping("/leagues")
    public String getLeaguesPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "leagues_page";
    }

    @GetMapping("/league/registration")
    public String getLeagueRegistrationPage(Model model) {
        model.addAttribute("emptyLeague", new CreateLeagueDto());
        return "league_registration";
    }

    @PostMapping("/league/registration")
    public String registerLeague(@Valid CreateLeagueDto createLeagueDto, Errors errors, Model model) {
        model.addAttribute("emptyLeague", new CreateLeagueDto());
        if (errors.hasErrors()) return "league_registration";
        try {
            leagueService.save(createLeagueDto);
        } catch (DuplicateLeagueNameException e) {
            model.addAttribute("duplicate_league_name_error", "League name " +
                    createLeagueDto.getName() + " is registered already");
            return "league_registration";
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    createLeagueDto.getCountryId() + " is not found");
            return "league_registration";
        }

        return "redirect:/leagues";
    }

    @GetMapping("/league/update/{id}")
    public String getLeagueUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateLeagueDtoToModel(id, model);
        return "league_update";
    }

    @PostMapping("/league/update/{id}")
    public String updateLeague(@PathVariable Integer id, @Valid UpdateLeagueDto updateLeagueDto, Errors errors, Model model) {
        updateLeagueDto.setId(id);
        putUpdateLeagueDtoToModel(id, model);
        if (errors.hasErrors()) return "league_update";
        try {
            leagueService.update(updateLeagueDto);
        } catch (DuplicateLeagueNameException e) {
            model.addAttribute("duplicate_league_name_error", "League name " +
                    updateLeagueDto.getName() + " is registered already");
            return "league_update";
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    updateLeagueDto.getCountryId() + " is not found");
            return "league_update";
        } catch (LeagueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/leagues";
    }

    @MessageMapping("/leagues/start")
    public void sendLeaguesOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetLeagueDto> leagues = leagueService.getByPage(page, LEAGUES_COUNT);
        messaging.convertAndSendToUser(username, LEAGUE_RETURN_URL, convertListToArray(leagues));
    }

    @MessageMapping("/leagues/next")
    public void sendLeaguesOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetLeagueDto> leagues = leagueService.getByPage(page, LEAGUES_COUNT);
        if (leagues.isEmpty()) {
            page = page - 1;
            leagues = leagueService.getByPage(page, LEAGUES_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, LEAGUE_RETURN_URL, convertListToArray(leagues));
    }

    @MessageMapping("/leagues/previous")
    public void sendLeaguesOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetLeagueDto> leagues = leagueService.getByPage(page, LEAGUES_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, LEAGUE_RETURN_URL, convertListToArray(leagues));
    }

    @MessageMapping("/leagues/delete/{id}")
    public void deleteLeague(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        leagueService.delete(id);
        List<GetLeagueDto> leagues = leagueService.getByPage(page, LEAGUES_COUNT);
        messaging.convertAndSendToUser(username, LEAGUE_RETURN_URL, convertListToArray(leagues));
    }

    private void putUpdateLeagueDtoToModel(Integer id, Model model) {
        try {
            GetLeagueDto getLeagueDto = leagueService.getById(id);
            UpdateLeagueDto updateLeagueDto = new UpdateLeagueDto(
                    getLeagueDto.getId(),
                    getLeagueDto.getName(),
                    getLeagueDto.getCountry().getId()
            );
            model.addAttribute("updateLeagueDto", updateLeagueDto);
        } catch (LeagueNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetLeagueDto[] convertListToArray(List<GetLeagueDto> list) {
        GetLeagueDto[] array = new GetLeagueDto[list.size()];
        return list.toArray(array);
    }

}
