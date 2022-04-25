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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "referees")
public class Referee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Pattern(regexp = "[0-9]+.(png|jpg|jpeg)")
    @Column(name = "image")
    private String image;

    @NotNull
    @ManyToOne
    private Country country;

    public Referee(Integer id, String firstName, String lastName, String image, Country country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Referee referee = (Referee) o;
        return id.equals(referee.id) && firstName.equals(referee.firstName) && lastName.equals(referee.lastName) && Objects.equals(image, referee.image) && country.equals(referee.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, image, country);
    }
}
