package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.country.CreateCountryDto;
import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.country.UpdateCountryDto;
import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.enums.Continent;
import by.bsuir.football.repository.CountryRepository;
import by.bsuir.football.service.CountryService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryCodeException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<GetCountryDto> getAll() {
        List<Country> countries = (List<Country>) countryRepository.findAll(Sort.by("name"));
        return countries.stream()
                .map(country -> new GetCountryDto(country.getId(), country.getName(), country.getCode(), country.getContinent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCountryDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("name"));
        List<Country> allCountriesByPage = countryRepository.findAll(pageable).toList();
        return allCountriesByPage
                .stream()
                .map(country -> new GetCountryDto(country.getId(), country.getName(), country.getCode(), country.getContinent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCountryDto> getAllByContinent(Continent continent) {
        return countryRepository.findAllByContinent(continent).stream()
                .map(country -> new GetCountryDto(country.getId(), country.getName(), country.getCode(), country.getContinent()))
                .collect(Collectors.toList());
    }


    @Override
    public GetCountryDto getById(Integer id) throws CountryNotFoundException {
        GetCountryDto countryDto = countryRepository.findById(id)
                .map(country -> new GetCountryDto(country.getId(), country.getName(), country.getCode(), country.getContinent()))
                .orElse(null);

        if (countryDto == null) {
            throw new CountryNotFoundException();
        }
        return countryDto;
    }

    @Override
    public void save(CreateCountryDto createCountryDto) throws DuplicateCountryNameException, DuplicateCountryCodeException {
        Country countryByName = countryRepository.findByName(createCountryDto.getName());
        if (countryByName != null) {
            throw new DuplicateCountryNameException();
        }

        Country countryByCode = countryRepository.findByCode(createCountryDto.getCode());
        if (countryByCode != null) {
            throw new DuplicateCountryCodeException();
        }

        Country country = new Country(createCountryDto.getName(), createCountryDto.getCode(), createCountryDto.getContinent());
        countryRepository.save(country);
    }

    @Override
    public void update(UpdateCountryDto updateCountryDto) throws CountryNotFoundException, DuplicateCountryNameException, DuplicateCountryCodeException {
        Country country = countryRepository.findById(updateCountryDto.getId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        Country countryByName = countryRepository.findByName(updateCountryDto.getName());
        if (countryByName != null && !countryByName.getId().equals(country.getId())) {
            throw new DuplicateCountryNameException();
        }

        Country countryByCode = countryRepository.findByCode(updateCountryDto.getCode());
        if (countryByCode != null && !countryByCode.getId().equals(country.getId())) {
            throw new DuplicateCountryCodeException();
        }

        country.setName(updateCountryDto.getName());
        country.setCode(updateCountryDto.getCode());
        country.setContinent(updateCountryDto.getContinent());
        countryRepository.save(country);
    }

    @Override
    public void delete(Integer id) {
        countryRepository.deleteById(id);
    }
}
