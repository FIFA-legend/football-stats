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
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Pattern(regexp = "[A-Z]{3}")
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Pattern(regexp = "[0-9]+.(png|jpg|jpeg)")
    @Column(name = "logo")
    private String logo;

    @NotNull
    @ManyToOne
    private Country country;

    public Team(Integer id, String fullName, String shortName, String logo, Country country) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.logo = logo;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id.equals(team.id) && fullName.equals(team.fullName) && shortName.equals(team.shortName) && Objects.equals(logo, team.logo) && country.equals(team.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, shortName, logo, country);
    }
}