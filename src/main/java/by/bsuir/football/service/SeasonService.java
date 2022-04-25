package by.bsuir.football.service;

import by.bsuir.football.dto.season.CreateSeasonDto;
import by.bsuir.football.dto.season.GetSeasonDto;
import by.bsuir.football.dto.season.UpdateSeasonDto;
import by.bsuir.football.service.exceptions.season.DuplicateSeasonNameException;
import by.bsuir.football.service.exceptions.season.SeasonNotFoundException;

import java.util.List;

public interface SeasonService {

    List<GetSeasonDto> getAll();

    List<GetSeasonDto> getByPage(int page, int count);

    GetSeasonDto getById(Integer id) throws SeasonNotFoundException;

    void save(CreateSeasonDto createSeasonDto) throws DuplicateSeasonNameException;

    void update(UpdateSeasonDto updateSeasonDto) throws SeasonNotFoundException, DuplicateSeasonNameException;

    void delete(Integer id);

}
