package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.Venue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface VenueRepository extends CrudRepository<Venue, Integer> {

    List<Venue> findAllByCountry(Country country);

}
