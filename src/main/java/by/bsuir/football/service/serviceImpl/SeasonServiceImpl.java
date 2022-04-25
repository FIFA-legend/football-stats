package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.season.CreateSeasonDto;
import by.bsuir.football.dto.season.GetSeasonDto;
import by.bsuir.football.dto.season.UpdateSeasonDto;
import by.bsuir.football.entity.Season;
import by.bsuir.football.repository.SeasonRepository;
import by.bsuir.football.service.SeasonService;
import by.bsuir.football.service.exceptions.season.DuplicateSeasonNameException;
import by.bsuir.football.service.exceptions.season.SeasonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;

    @Autowired
    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public List<GetSeasonDto> getAll() {
        List<Season> seasons = (List<Season>) seasonRepository.findAll(Sort.by("name"));
        return seasons.stream()
                .map(season -> new GetSeasonDto(season.getId(), season.getIsCurrent(), season.getName(), season.getStartDate(), season.getEndDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetSeasonDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("name"));
        List<Season> allSeasonsByPage = seasonRepository.findAll(pageable).toList();
        return allSeasonsByPage.stream()
                .map(season -> new GetSeasonDto(season.getId(), season.getIsCurrent(), season.getName(), season.getStartDate(), season.getEndDate()))
                .collect(Collectors.toList());
    }

    @Override
    public GetSeasonDto getById(Integer id) throws SeasonNotFoundException {
        GetSeasonDto seasonDto = seasonRepository.findById(id)
                .map(season -> new GetSeasonDto(season.getId(), season.getIsCurrent(), season.getName(), season.getStartDate(), season.getEndDate()))
                .orElse(null);

        if (seasonDto == null) {
            throw new SeasonNotFoundException();
        }
        return seasonDto;
    }

    @Override
    public void save(CreateSeasonDto createSeasonDto) throws DuplicateSeasonNameException {
        Season seasonByName = seasonRepository.findByName(createSeasonDto.getName());
        if (seasonByName != null) {
            throw new DuplicateSeasonNameException();
        }

        LocalDate now = LocalDate.now();
        boolean isCurrent = now.isAfter(createSeasonDto.getStartDate()) && now.isBefore(createSeasonDto.getEndDate());

        Season season = new Season(isCurrent, createSeasonDto.getName(), createSeasonDto.getStartDate(), createSeasonDto.getEndDate());
        seasonRepository.save(season);
    }

    @Override
    public void update(UpdateSeasonDto updateSeasonDto) throws SeasonNotFoundException, DuplicateSeasonNameException {
        Season season = seasonRepository.findById(updateSeasonDto.getId()).orElse(null);
        if (season == null) {
            throw new SeasonNotFoundException();
        }

        Season seasonByName = seasonRepository.findByName(updateSeasonDto.getName());
        if (seasonByName != null && !seasonByName.getId().equals(season.getId())) {
            throw new DuplicateSeasonNameException();
        }

        LocalDate now = LocalDate.now();
        boolean isCurrent = now.isAfter(updateSeasonDto.getStartDate()) && now.isBefore(updateSeasonDto.getEndDate());

        season.setIsCurrent(isCurrent);
        season.setName(updateSeasonDto.getName());
        season.setStartDate(updateSeasonDto.getStartDate());
        season.setEndDate(updateSeasonDto.getEndDate());
        seasonRepository.save(season);
    }

    @Override
    public void delete(Integer id) {
        seasonRepository.deleteById(id);
    }
}
