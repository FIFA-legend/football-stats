package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.Team;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TeamRepository extends PagingAndSortingRepository<Team, Integer> {

    List<Team> findAllByCountry(Country country);

    List<Team> findAllByFullName(String fullName);

    void deleteByFullName(String fullName);

}
