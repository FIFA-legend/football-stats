package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.dto.country.GetCountryDto;
import by.bsuir.football.dto.venue.CreateVenueDto;
import by.bsuir.football.dto.venue.GetVenueDto;
import by.bsuir.football.dto.venue.UpdateVenueDto;
import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.Venue;
import by.bsuir.football.repository.CountryRepository;
import by.bsuir.football.repository.VenueRepository;
import by.bsuir.football.service.VenueService;
import by.bsuir.football.service.exceptions.country.CountryNotFoundException;
import by.bsuir.football.service.exceptions.venue.DuplicateVenueNameException;
import by.bsuir.football.service.exceptions.venue.VenueNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

    private final CountryRepository countryRepository;

    private final VenueRepository venueRepository;

    @Autowired
    public VenueServiceImpl(CountryRepository countryRepository, VenueRepository venueRepository) {
        this.countryRepository = countryRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    public List<GetVenueDto> getAll() {
        List<Venue> venues = (List<Venue>) venueRepository.findAll(Sort.by("country_id", "name"));
        return venues.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetVenueDto> getByPage(int page, int count) {
        Pageable pageable = PageRequest.of(page, count, Sort.by("country_id", "name"));
        List<Venue> allVenuesByPage = venueRepository.findAll(pageable).toList();
        return allVenuesByPage.stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetVenueDto> getAllByCountry(Integer countryId) throws CountryNotFoundException {
        Country country = countryRepository.findById(countryId).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        return venueRepository.findAllByCountry(country).stream()
                .map(this::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetVenueDto getById(Integer id) throws VenueNotFoundException {
        GetVenueDto venueDto = venueRepository.findById(id)
                .map(this::convertDomainToDto)
                .orElse(null);

        if (venueDto == null) {
            throw new VenueNotFoundException();
        }
        return venueDto;
    }

    @Override
    public void save(CreateVenueDto createVenueDto) throws CountryNotFoundException, DuplicateVenueNameException {
        Venue venueByName = venueRepository.findByName(createVenueDto.getName());
        if (venueByName != null) {
            throw new DuplicateVenueNameException();
        }

        Country country = countryRepository.findById(createVenueDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        Venue venue = new Venue(createVenueDto.getName(), createVenueDto.getCapacity(), createVenueDto.getCity(), country);
        venueRepository.save(venue);
    }

    @Override
    public void update(UpdateVenueDto updateVenueDto) throws VenueNotFoundException, CountryNotFoundException, DuplicateVenueNameException {
        Venue venue = venueRepository.findById(updateVenueDto.getId()).orElse(null);
        if (venue == null) {
            throw new VenueNotFoundException();
        }

        Country country = countryRepository.findById(updateVenueDto.getCountryId()).orElse(null);
        if (country == null) {
            throw new CountryNotFoundException();
        }

        Venue venueByName = venueRepository.findByName(updateVenueDto.getName());
        if (venueByName != null && !venueByName.getId().equals(venue.getId())) {
            throw new DuplicateVenueNameException();
        }

        venue.setName(updateVenueDto.getName());
        venue.setCapacity(updateVenueDto.getCapacity());
        venue.setCity(updateVenueDto.getCity());
        venue.setCountry(country);
        venueRepository.save(venue);
    }

    @Override
    public void delete(Integer id) {
        venueRepository.deleteById(id);
    }

    private GetVenueDto convertDomainToDto(Venue venue) {
        return new GetVenueDto(
                venue.getId(),
                venue.getName(),
                venue.getCapacity(),
                venue.getCity(),
                new GetCountryDto(
                        venue.getCountry().getId(),
                        venue.getCountry().getName(),
                        venue.getCountry().getCode(),
                        venue.getCountry().getContinent()
                )
        );
    }
}
