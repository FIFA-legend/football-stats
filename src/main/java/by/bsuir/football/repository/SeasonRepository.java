package by.bsuir.football.repository;

import by.bsuir.football.entity.Season;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SeasonRepository extends CrudRepository<Season, Integer> {
}
