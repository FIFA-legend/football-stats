package by.bsuir.football.service;

import by.bsuir.football.dto.venue.CreateVenueDto;
import by.bsuir.football.dto.venue.GetVenueDto;
import by.bsuir.football.dto.venue.UpdateVenueDto;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.venue.DuplicateVenueNameException;
import by.bsuir.football.service.exceptions.venue.VenueNotFoundException;

import java.util.List;

public interface VenueService {

    List<GetVenueDto> getAll();

    List<GetVenueDto> getByPage(int page, int count);

    List<GetVenueDto> getAllByCountry(Integer countryId) throws CountryNotFoundException;

    GetVenueDto getById(Integer id) throws VenueNotFoundException;

    void save(CreateVenueDto createVenueDto) throws CountryNotFoundException, DuplicateVenueNameException;

    void update(UpdateVenueDto updateVenueDto) throws VenueNotFoundException, CountryNotFoundException, DuplicateVenueNameException;

    void delete(Integer id);

}
