package by.bsuir.football.repository;

import by.bsuir.football.entity.Season;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SeasonRepository extends PagingAndSortingRepository<Season, Integer> {

    Season findByName(String name);

}
