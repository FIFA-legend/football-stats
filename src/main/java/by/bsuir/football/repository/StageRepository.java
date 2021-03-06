package by.bsuir.football.repository;

import by.bsuir.football.entity.Stage;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StageRepository extends PagingAndSortingRepository<Stage, Integer> {

    Stage findByName(String name);

}
