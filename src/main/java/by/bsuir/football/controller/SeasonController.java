package by.bsuir.football.controller;

import by.bsuir.football.dto.season.CreateSeasonDto;
import by.bsuir.football.dto.season.GetSeasonDto;
import by.bsuir.football.dto.season.UpdateSeasonDto;
import by.bsuir.football.service.SeasonService;
import by.bsuir.football.service.exceptions.season.DuplicateSeasonNameException;
import by.bsuir.football.service.exceptions.season.SeasonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SeasonController {

    private final int SEASONS_COUNT = 10;

    private final String SEASON_RETURN_URL = "/queue/seasons/return";

    private final SeasonService seasonService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public SeasonController(SeasonService seasonService, SimpMessagingTemplate messaging) {
        this.seasonService = seasonService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @GetMapping("/seasons")
    public String getSeasonsPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "seasons_page";
    }

    @GetMapping("/season/registration")
    public String getSeasonRegistrationPage(Model model) {
        model.addAttribute("emptySeason", new CreateSeasonDto());
        return "season_registration";
    }

    @PostMapping("/season/registration")
    public String registerSeason(@Valid CreateSeasonDto createSeasonDto, Errors errors, Model model) {
        model.addAttribute("emptySeason", createSeasonDto);
        if (errors.hasErrors()) return "season_registration";
        try {
            seasonService.save(createSeasonDto);
        } catch (DuplicateSeasonNameException e) {
            model.addAttribute("duplicate_season_name_error", "Season name " +
                    createSeasonDto.getName() + " is registered already");
            return "season_registration";
        }

        return "redirect:/seasons";
    }

    @GetMapping("/season/update/{id}")
    public String getSeasonUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateSeasonDtoToModel(id, model);
        return "season_update";
    }

    @PostMapping("/season/update/{id}")
    public String updateSeason(@PathVariable Integer id, @Valid UpdateSeasonDto updateSeasonDto, Errors errors, Model model) {
        updateSeasonDto.setId(id);
        putUpdateSeasonDtoToModel(id, model);
        if (errors.hasErrors()) return "season_update";
        try {
            seasonService.update(updateSeasonDto);
        }  catch (DuplicateSeasonNameException e) {
            model.addAttribute("duplicate_season_name_error", "Season name " +
                    updateSeasonDto.getName() + " is registered already");
            return "season_update";
        } catch (SeasonNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/seasons";
    }

    @MessageMapping("/seasons/start")
    public void sendSeasonsOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetSeasonDto> seasons = seasonService.getByPage(page, SEASONS_COUNT);
        messaging.convertAndSendToUser(username, SEASON_RETURN_URL, convertListToArray(seasons));
    }

    @MessageMapping("/seasons/next")
    public void sendSeasonsOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetSeasonDto> seasons = seasonService.getByPage(page, SEASONS_COUNT);
        if (seasons.isEmpty()) {
            page = page - 1;
            seasons = seasonService.getByPage(page, SEASONS_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, SEASON_RETURN_URL, convertListToArray(seasons));
    }

    @MessageMapping("/seasons/previous")
    public void sendSeasonsOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetSeasonDto> seasons = seasonService.getByPage(page, SEASONS_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, SEASON_RETURN_URL, convertListToArray(seasons));
    }

    @MessageMapping("/seasons/delete/{id}")
    public void deleteSeason(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        seasonService.delete(id);
        List<GetSeasonDto> seasons = seasonService.getByPage(page, SEASONS_COUNT);
        messaging.convertAndSendToUser(username, SEASON_RETURN_URL, convertListToArray(seasons));
    }

    private void putUpdateSeasonDtoToModel(Integer id, Model model) {
        try {
            GetSeasonDto getSeasonDto = seasonService.getById(id);
            UpdateSeasonDto updateSeasonDto = new UpdateSeasonDto(
                    getSeasonDto.getId(),
                    getSeasonDto.getName(),
                    getSeasonDto.getStartDate(),
                    getSeasonDto.getEndDate()
            );
            model.addAttribute("updateSeasonDto", updateSeasonDto);
        } catch (SeasonNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetSeasonDto[] convertListToArray(List<GetSeasonDto> list) {
        GetSeasonDto[] array = new GetSeasonDto[list.size()];
        return list.toArray(array);
    }

}
