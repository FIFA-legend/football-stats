package by.bsuir.football.service;

import by.bsuir.football.dto.team.CreateTeamDto;
import by.bsuir.football.dto.team.GetTeamDto;
import by.bsuir.football.dto.team.UpdateTeamDto;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamImageNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeamService {

    List<GetTeamDto> getAll();

    List<GetTeamDto> getByPage(int page, int count);

    List<GetTeamDto> getAllByCountry(Integer countryId) throws CountryNotFoundException;

    GetTeamDto getById(Integer id) throws TeamNotFoundException;

    void save(CreateTeamDto createTeamDto, String username) throws CountryNotFoundException;

    void update(UpdateTeamDto updateTeamDto) throws TeamNotFoundException, CountryNotFoundException;

    void delete(Integer id);

    Byte[] getLogo(Integer id) throws TeamImageNotFoundException;

    void saveLogo(MultipartFile file, Integer id, String username);

}
