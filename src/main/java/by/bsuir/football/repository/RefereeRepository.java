package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.Referee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RefereeRepository extends CrudRepository<Referee, Integer> {

    List<Referee> findAllByCountry(Country country);

}
