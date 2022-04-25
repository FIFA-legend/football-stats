package by.bsuir.football.entity;

import by.bsuir.football.entity.enums.Continent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Pattern(regexp = "[A-Z][A-Za-z]+")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Pattern(regexp = "[a-z]{2}")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "continent", nullable = false)
    private Continent continent;

    public Country(String name, String code, Continent continent) {
        this.name = name;
        this.code = code;
        this.continent = continent;
    }

    public Country(Integer id, String name, String code, Continent continent) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.continent = continent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return id.equals(country.id) && name.equals(country.name) && code.equals(country.code) && continent == country.continent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, continent);
    }
}
