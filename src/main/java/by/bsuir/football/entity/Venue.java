package by.bsuir.football.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Min(0)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotEmpty
    @Column(name = "city", nullable = false)
    private String city;

    @NotNull
    @ManyToOne
    private Country country;

    public Venue(Integer id, String name, Integer capacity, String city, Country country) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.city = city;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return id.equals(venue.id) && name.equals(venue.name) && capacity.equals(venue.capacity) && city.equals(venue.city) && country.equals(venue.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity, city, country);
    }
}
