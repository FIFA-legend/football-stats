package by.bsuir.football.controller;

import by.bsuir.football.dto.stage.CreateStageDto;
import by.bsuir.football.dto.stage.GetStageDto;
import by.bsuir.football.dto.stage.UpdateStageDto;
import by.bsuir.football.service.StageService;
import by.bsuir.football.service.exceptions.stage.DuplicateStageNameException;
import by.bsuir.football.service.exceptions.stage.StageNotFoundException;
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
public class StageController {

    private final int STAGES_COUNT = 10;

    private final String STAGE_RETURN_URL = "/queue/stages/return";

    private final StageService stageService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public StageController(StageService stageService, SimpMessagingTemplate messaging) {
        this.stageService = stageService;
        this.messaging = messaging;
        this.map = new HashMap<>();
    }

    @GetMapping("/stages")
    public String getStagesPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "stages_page";
    }

    @GetMapping("/stage/registration")
    public String getStageRegistrationPage(Model model) {
        model.addAttribute("emptyStage", new CreateStageDto());
        return "stage_registration";
    }

    @PostMapping("/stage/registration")
    public String registerStage(@Valid CreateStageDto createStageDto, Errors errors, Model model) {
        model.addAttribute("emptyStage", createStageDto);
        if (errors.hasErrors()) return "stage_registration";
        try {
            stageService.save(createStageDto);
        } catch (DuplicateStageNameException e) {
            model.addAttribute("duplicate_stage_name_error", "Stage name " +
                    createStageDto.getName() + " is registered already");
            return "stage_registration";
        }

        return "redirect:/stages";
    }

    @GetMapping("/stage/update/{id}")
    public String getStageUpdatePage(@PathVariable Integer id, Model model) {
        putUpdateStageDtoToModel(id, model);
        return "stage_update";
    }

    @PostMapping("/stage/update/{id}")
    public String updateStage(@PathVariable Integer id, @Valid UpdateStageDto updateStageDto, Errors errors, Model model) {
        updateStageDto.setId(id);
        putUpdateStageDtoToModel(id, model);
        if (errors.hasErrors()) return "stage_update";
        try {
            stageService.update(updateStageDto);
        }  catch (DuplicateStageNameException e) {
            model.addAttribute("duplicate_stage_name_error", "Stage name " +
                    updateStageDto.getName() + " is registered already");
            return "stage_update";
        } catch (StageNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/stages";
    }

    @MessageMapping("/stages/start")
    public void sendStagesOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetStageDto> stages = stageService.getByPage(page, STAGES_COUNT);
        messaging.convertAndSendToUser(username, STAGE_RETURN_URL, convertListToArray(stages));
    }

    @MessageMapping("/stages/next")
    public void sendStagesOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetStageDto> stages = stageService.getByPage(page, STAGES_COUNT);
        if (stages.isEmpty()) {
            page = page - 1;
            stages = stageService.getByPage(page, STAGES_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, STAGE_RETURN_URL, convertListToArray(stages));
    }

    @MessageMapping("/stages/previous")
    public void sendStagesOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetStageDto> stages = stageService.getByPage(page, STAGES_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, STAGE_RETURN_URL, convertListToArray(stages));
    }

    @MessageMapping("/stages/delete/{id}")
    public void deleteStage(@DestinationVariable Integer id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        stageService.delete(id);
        List<GetStageDto> stages = stageService.getByPage(page, STAGES_COUNT);
        messaging.convertAndSendToUser(username, STAGE_RETURN_URL, convertListToArray(stages));
    }

    private void putUpdateStageDtoToModel(Integer id, Model model) {
        try {
            GetStageDto getStageDto = stageService.getById(id);
            UpdateStageDto updateStageDto = new UpdateStageDto(
                    getStageDto.getId(),
                    getStageDto.getName()
            );
            model.addAttribute("updateStageDto", updateStageDto);
        } catch (StageNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private GetStageDto[] convertListToArray(List<GetStageDto> list) {
        GetStageDto[] array = new GetStageDto[list.size()];
        return list.toArray(array);
    }

}
