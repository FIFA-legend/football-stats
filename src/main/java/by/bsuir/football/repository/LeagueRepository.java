package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.League;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LeagueRepository extends PagingAndSortingRepository<League, Integer> {

    List<League> findAllByCountry(Country country);

    League findByName(String name);

}
