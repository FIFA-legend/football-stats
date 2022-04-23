package by.bsuir.football.service;

import by.bsuir.football.dto.country.CreateCountryDto;
import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.country.UpdateCountryDto;
import by.bsuir.football.entity.enums.Continent;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryCodeException;
import by.bsuir.football.service.exceptions.country.DuplicateCountryNameException;

import java.util.List;

public interface CountryService {

    List<GetCountryDto> getAll();

    List<GetCountryDto> getAllByContinent(Continent continent);

    GetCountryDto getById(Integer id) throws CountryNotFoundException;

    void save(CreateCountryDto createCountryDto) throws DuplicateCountryNameException, DuplicateCountryCodeException;

    void update(UpdateCountryDto updateCountryDto) throws CountryNotFoundException, DuplicateCountryNameException, DuplicateCountryCodeException;

    void delete(Integer id);

}
