package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.stage.CreateStageDto;
import by.bsuir.football.dto.stage.GetStageDto;
import by.bsuir.football.dto.stage.UpdateStageDto;
import by.bsuir.football.entity.Stage;
import by.bsuir.football.repository.StageRepository;
import by.bsuir.football.service.StageService;
import by.bsuir.football.service.exceptions.stage.DuplicateStageNameException;
import by.bsuir.football.service.exceptions.stage.StageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StageServiceImpl implements StageService {

    private final StageRepository stageRepository;

    @Autowired
    public StageServiceImpl(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public List<GetStageDto> getAll() {
        List<Stage> stages = (List<Stage>) stageRepository.findAll(Sort.by("name"));
        return stages.stream()
                .map(stage -> new GetStageDto(stage.getId(), stage.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetStageDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("name"));
        List<Stage> allStagesByPage = stageRepository.findAll(pageable).toList();
        return allStagesByPage.stream()
                .map(stage -> new GetStageDto(stage.getId(), stage.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public GetStageDto getById(Integer id) throws StageNotFoundException {
        GetStageDto stageDto = stageRepository.findById(id)
                .map(stage -> new GetStageDto(stage.getId(), stage.getName()))
                .orElse(null);

        if (stageDto == null) {
            throw new StageNotFoundException();
        }
        return stageDto;
    }

    @Override
    public void save(CreateStageDto createStageDto) throws DuplicateStageNameException {
        Stage stageByName = stageRepository.findByName(createStageDto.getName());
        if (stageByName != null) {
            throw new DuplicateStageNameException();
        }

        Stage stage = new Stage(createStageDto.getName());
        stageRepository.save(stage);
    }

    @Override
    public void update(UpdateStageDto updateStageDto) throws StageNotFoundException, DuplicateStageNameException {
        Stage stage = stageRepository.findById(updateStageDto.getId()).orElse(null);
        if (stage == null) {
            throw new StageNotFoundException();
        }

        Stage stageByName = stageRepository.findByName(updateStageDto.getName());
        if (stageByName != null && !stageByName.getId().equals(stage.getId())) {
            throw new DuplicateStageNameException();
        }

        stage.setName(updateStageDto.getName());
        stageRepository.save(stage);
    }

    @Override
    public void delete(Integer id) {
        stageRepository.deleteById(id);
    }
}
