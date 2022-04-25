package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.team.CreateTeamDto;
import by.bsuir.football.dto.team.GetTeamDto;
import by.bsuir.football.dto.team.UpdateTeamDto;
import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.Team;
import by.bsuir.football.repository.CountryRepository;
import by.bsuir.football.repository.TeamRepository;
import by.bsuir.football.service.TeamService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamImageNotFoundException;
import by.bsuir.football.service.exceptions.team.TeamNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {

    private final CountryRepository countryRepository;

    private final TeamRepository teamRepository;

    @Autowired
    public TeamServiceImpl(CountryRepository countryRepository, TeamRepository teamRepository) {
        this.countryRepository = countryRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<GetTeamDto> getAll() {
        List<Team> teams = (List<Team>) teamRepository.findAll(Sort.by("country_id", "name"));
        return teams.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTeamDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("country_id", "fullName"));
        List<Team> allTeamsByPage = teamRepository.findAll(pageable).toList();
        return allTeamsByPage.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTeamDto> getAllByCountry(Integer countryId) throws CountryNotFoundException {
        Country country = countryRepository.findById(countryId).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        return teamRepository.findAllByCountry(country).stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetTeamDto getById(Integer id) throws TeamNotFoundException {
        GetTeamDto teamDto = teamRepository.findById(id)
                .map(this::convertDomainToDto)
                .orElse(null);

        if (teamDto == null) {
            throw new TeamNotFoundException();
        }
        return teamDto;
    }

    @Override
    public void save(CreateTeamDto createTeamDto, String username) throws CountryNotFoundException {
        Country country = countryRepository.findById(createTeamDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        List<Team> allTempTeams = teamRepository.findAllByFullName(username);
        Team temp = allTempTeams.get(allTempTeams.size() - 1);
        Byte[] image = null;
        if (temp != null) image = temp.getLogo();

        Team team = new Team(createTeamDto.getFullName(), createTeamDto.getShortName(), image, country);
        teamRepository.save(team);
        teamRepository.deleteByFullName(username);
    }

    @Override
    public void update(UpdateTeamDto updateTeamDto) throws TeamNotFoundException, CountryNotFoundException {
        Team team = teamRepository.findById(updateTeamDto.getId()).orElse(null);
        if (team == null) {
            throw new TeamNotFoundException();
        }

        Country country = countryRepository.findById(updateTeamDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        team.setFullName(updateTeamDto.getFullName());
        team.setShortName(updateTeamDto.getShortName());
        team.setCountry(country);
        teamRepository.save(team);
    }

    @Override
    public void delete(Integer id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Byte[] getLogo(Integer id) throws TeamImageNotFoundException {
        Team team = teamRepository.findById(id).orElse(null);
        if (team == null || team.getLogo() == null) {
            throw new TeamImageNotFoundException();
        }

        return team.getLogo();
    }

    @Override
    public void saveLogo(MultipartFile file, Integer id, String username) {
        try {
            Team team = teamRepository.findById(id).orElse(null);
            Byte[] bytes = new Byte[file.getBytes().length];
            int i = 0;
            for (byte b : file.getBytes()) {
                bytes[i++] = b;
            }

            if (team == null) {
                Country country = ((List<Country>) countryRepository.findAll()).get(0);
                Team temp = new Team(username, "XXX", bytes, country);
                teamRepository.save(temp);
            } else {
                team.setLogo(bytes);
                teamRepository.save(team);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GetTeamDto convertDomainToDto(Team team) {
        return new GetTeamDto(
                team.getId(),
                team.getFullName(),
                team.getShortName(),
                new GetCountryDto(
                        team.getCountry().getId(),
                        team.getCountry().getName(),
                        team.getCountry().getCode(),
                        team.getCountry().getContinent()
                ),
                team.getLogo() != null
        );
    }
}
