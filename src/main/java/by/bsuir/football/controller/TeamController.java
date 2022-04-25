package by.bsuir.football.controller;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.team.CreateTeamDto;
import by.bsuir.football.dto.team.GetTeamDto;
import by.bsuir.football.dto.team.UpdateTeamDto;
import by.bsuir.football.service.CountryService;
import by.bsuir.football.service.TeamService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamImageNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamNotFoundException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TeamController {

    private final int TEAMS_COUNT = 10;

    private final String TEAM_RETURN_URL = "/queue/teams/return";

    private final CountryService countryService;

    private final TeamService teamService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public TeamController(CountryService countryService, TeamService teamService, SimpMessagingTemplate messaging) {
        this.countryService = countryService;
        this.teamService = teamService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @ModelAttribute("max_value")
    public int maxValue() {
        return Integer.MAX_VALUE;
    }

    @ModelAttribute("countries")
    public GetCountryDto[] countries() {
        List<GetCountryDto> countries = countryService.getAll();
        GetCountryDto[] array = new GetCountryDto[countries.size()];
        return countries.toArray(array);
    }

    @GetMapping("/teams")
    public String getTeamsPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "teams_page";
    }

    @GetMapping("/team/registration")
    public String getTeamRegistrationPage(Model model) {
        model.addAttribute("emptyTeam", new CreateTeamDto());
        return "team_registration";
    }

    @PostMapping("/team/registration")
    public String registerTeam(@Valid CreateTeamDto createTeamDto, Errors errors, Model model, Principal principal) {
        model.addAttribute("emptyTeam", new CreateTeamDto());
        if (errors.hasErrors()) return "team_registration";
        try {
            teamService.save(createTeamDto, principal.getName());
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    createTeamDto.getCountryId() + " is not found");
            return "team_registration";
        }

        return "redirect:/teams";
    }

    @GetMapping("/team/update/{id}")
    public String getTeamUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateTeamDtoToModel(id, model);
        return "team_update";
    }

    @PostMapping("/team/update/{id}")
    public String updateTeam(@PathVariable Integer id, @Valid UpdateTeamDto updateTeamDto, Errors errors, Model model) {
        updateTeamDto.setId(id);
        putUpdateTeamDtoToModel(id, model);
        if (errors.hasErrors()) return "team_update";
        try {
            teamService.update(updateTeamDto);
        } catch (CountryNotFoundException e) {
            model.addAttribute("country_not_found_error", "Country with id " +
                    updateTeamDto.getCountryId() + " is not found");
            return "team_update";
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "redirect:/teams";
    }

    @GetMapping("/team/{id}/image")
    public void showTeamLogo(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        try {
            Byte[] logo = teamService.getLogo(id);

            byte[] bytes = new byte[logo.length];
            int i = 0;
            for (Byte wrappedByte : logo) {
                bytes[i++] = wrappedByte;
            }

            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(bytes);
            IOUtils.copy(is, response.getOutputStream());
        } catch (TeamImageNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/team/{id}/image")
    public String saveTeamLogo(@PathVariable Integer id, @RequestParam("image_file") MultipartFile file, Model model, Principal principal) {
        teamService.saveLogo(file, id, principal.getName());
        if (id == Integer.MAX_VALUE) {
            model.addAttribute("emptyTeam", new CreateTeamDto());
            return "team_registration";
        } else {
            putUpdateTeamDtoToModel(id, model);
            return "team_update";
        }
    }

    @MessageMapping("/teams/start")
    public void sendTeamsOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetTeamDto> teams = teamService.getByPage(page, TEAMS_COUNT);
        messaging.convertAndSendToUser(username, TEAM_RETURN_URL, convertListToArray(teams));
    }

    @MessageMapping("/teams/next")
    public void sendTeamsOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetTeamDto> teams = teamService.getByPage(page, TEAMS_COUNT);
        if (teams.isEmpty()) {
            page = page - 1;
            teams = teamService.getByPage(page, TEAMS_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, TEAM_RETURN_URL, convertListToArray(teams));
    }

    @MessageMapping("/teams/previous")
    public void sendTeamsOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetTeamDto> teams = teamService.getByPage(page, TEAMS_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, TEAM_RETURN_URL, convertListToArray(teams));
    }

    @MessageMapping("/teams/delete/{id}")
    public void deleteTeam(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        teamService.delete(id);
        List<GetTeamDto> teams = teamService.getByPage(page, TEAMS_COUNT);
        messaging.convertAndSendToUser(username, TEAM_RETURN_URL, convertListToArray(teams));
    }

    private void putUpdateTeamDtoToModel(Integer id, Model model) {
        try {
            GetTeamDto getTeamDto = teamService.getById(id);
            UpdateTeamDto updateTeamDto = new UpdateTeamDto(
                    getTeamDto.getId(),
                    getTeamDto.getFullName(),
                    getTeamDto.getShortName(),
                    getTeamDto.getCountry().getId(),
                    getTeamDto.isHasLogo()
            );
            model.addAttribute("updateTeamDto", updateTeamDto);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetTeamDto[] convertListToArray(List<GetTeamDto> list) {
        GetTeamDto[] array = new GetTeamDto[list.size()];
        return list.toArray(array);
    }

}
