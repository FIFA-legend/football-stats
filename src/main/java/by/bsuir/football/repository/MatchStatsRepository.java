package by.bsuir.football.repository;

import by.bsuir.football.entity.MatchStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MatchStatsRepository extends CrudRepository<MatchStats, Long> {
}
