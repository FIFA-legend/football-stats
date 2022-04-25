package by.bsuir.football.service;

import by.bsuir.football.dto.stage.CreateStageDto;
import by.bsuir.football.dto.stage.GetStageDto;
import by.bsuir.football.dto.stage.UpdateStageDto;
import by.bsuir.football.service.exceptions.stage.DuplicateStageNameException;
import by.bsuir.football.service.exceptions.stage.StageNotFoundException;

import java.util.List;

public interface StageService {

    List<GetStageDto> getAll();

    List<GetStageDto> getByPage(int page, int count);

    GetStageDto getById(Integer id) throws StageNotFoundException;

    void save(CreateStageDto createStageDto) throws DuplicateStageNameException;

    void update(UpdateStageDto updateStageDto) throws StageNotFoundException, DuplicateStageNameException;

    void delete(Integer id);

}
