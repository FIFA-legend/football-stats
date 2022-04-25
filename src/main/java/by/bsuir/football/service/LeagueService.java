package by.bsuir.football.service;

import by.bsuir.football.dto.league.CreateLeagueDto;
import by.bsuir.football.dto.league.GetLeagueDto;
import by.bsuir.football.dto.league.UpdateLeagueDto;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.league.DuplicateLeagueNameException;
import by.bsuir.football.service.exceptions.league.LeagueNotFoundException;

import java.util.List;

public interface LeagueService {

    List<GetLeagueDto> getAll();

    List<GetLeagueDto> getByPage(int page, int count);

    List<GetLeagueDto> getAllByCountry(Integer countryId) throws CountryNotFoundException;

    GetLeagueDto getById(Integer id) throws LeagueNotFoundException;

    void save(CreateLeagueDto createLeagueDto) throws CountryNotFoundException, DuplicateLeagueNameException;

    void update(UpdateLeagueDto updateLeagueDto) throws LeagueNotFoundException, CountryNotFoundException, DuplicateLeagueNameException;

    void delete(Integer id);

}
