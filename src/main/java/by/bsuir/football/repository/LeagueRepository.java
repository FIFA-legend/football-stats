package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.League;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LeagueRepository extends CrudRepository<League, Integer> {

    List<League> findAllByCountry(Country country);

}
