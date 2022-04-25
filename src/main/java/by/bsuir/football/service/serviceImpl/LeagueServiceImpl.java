package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.league.CreateLeagueDto;
import by.bsuir.football.dto.league.GetLeagueDto;
import by.bsuir.football.dto.league.UpdateLeagueDto;
import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.League;
import by.bsuir.football.repository.CountryRepository;
import by.bsuir.football.repository.LeagueRepository;
import by.bsuir.football.service.LeagueService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.league.DuplicateLeagueNameException;
import by.bsuir.football.service.exceptions.league.LeagueNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeagueServiceImpl implements LeagueService {

    private final CountryRepository countryRepository;

    private final LeagueRepository leagueRepository;

    @Autowired
    public LeagueServiceImpl(CountryRepository countryRepository, LeagueRepository leagueRepository) {
        this.countryRepository = countryRepository;
        this.leagueRepository = leagueRepository;
    }

    @Override
    public List<GetLeagueDto> getAll() {
        List<League> leagues = (List<League>) leagueRepository.findAll(Sort.by("country_id", "name"));
        return leagues.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetLeagueDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("country_id", "name"));
        List<League> allLeaguesByPage = leagueRepository.findAll(pageable).toList();
        return allLeaguesByPage.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetLeagueDto> getAllByCountry(Integer countryId) throws CountryNotFoundException {
        Country country = countryRepository.findById(countryId).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        return leagueRepository.findAllByCountry(country).stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetLeagueDto getById(Integer id) throws LeagueNotFoundException {
        GetLeagueDto leagueDto = leagueRepository.findById(id)
                .map(this::convertDomainToDto)
                .orElse(null);

        if (leagueDto == null) {
            throw new LeagueNotFoundException();
        }
        return leagueDto;
    }

    @Override
    public void save(CreateLeagueDto createLeagueDto) throws CountryNotFoundException, DuplicateLeagueNameException {
        League leagueByName = leagueRepository.findByName(createLeagueDto.getName());
        if (leagueByName != null) {
            throw new DuplicateLeagueNameException();
        }

        Country country = countryRepository.findById(createLeagueDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        League league = new League(createLeagueDto.getName(), country);
        leagueRepository.save(league);
    }

    @Override
    public void update(UpdateLeagueDto updateLeagueDto) throws LeagueNotFoundException, CountryNotFoundException, DuplicateLeagueNameException {
        League league = leagueRepository.findById(updateLeagueDto.getId()).orElse(null);
        if (league == null) {
            throw new LeagueNotFoundException();
        }

        Country country = countryRepository.findById(updateLeagueDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        League leagueByName = leagueRepository.findByName(updateLeagueDto.getName());
        if (leagueByName != null && !leagueByName.getId().equals(league.getId())) {
            throw new DuplicateLeagueNameException();
        }

        league.setName(updateLeagueDto.getName());
        league.setCountry(country);
        leagueRepository.save(league);
    }

    @Override
    public void delete(Integer id) {
        leagueRepository.deleteById(id);
    }

    private GetLeagueDto convertDomainToDto(League league) {
        return new GetLeagueDto(
                league.getId(),
                league.getName(),
                new GetCountryDto(
                        league.getCountry().getId(),
                        league.getCountry().getName(),
                        league.getCountry().getCode(),
                        league.getCountry().getContinent()
                )
        );
    }
}
