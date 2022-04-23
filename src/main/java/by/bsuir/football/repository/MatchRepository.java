package by.bsuir.football.repository;

import by.bsuir.football.entity.Match;
import by.bsuir.football.entity.Season;
import by.bsuir.football.entity.enums.Status;
import by.bsuir.football.repository.filtration.MatchFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> getAllBySeason(Season season);

    List<Match> getAllByStatus(Status status);

    List<Match> getAllByStartDate(LocalDate startDate);

    List<Match> getAllByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Match> getAllByStartDateOrderByLeague(LocalDate startDate);

    List<Match> getAllByParameters(MatchFilter filter);

}
