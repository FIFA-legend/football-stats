package by.bsuir.football.repository;

import by.bsuir.football.entity.Country;
import by.bsuir.football.entity.enums.Continent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CountryRepository extends CrudRepository<Country, Integer> {

    List<Country> findAllByContinent(Continent continent);

    Country findByName(String name);

    Country findByCode(String code);

}
