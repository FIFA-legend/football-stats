package by.bsuir.football.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "seasons")
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent;

    @Pattern(regexp = "[0-9]{4}/[0-9]{4}")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public Season(Boolean isCurrent, String name, LocalDate startDate, LocalDate endDate) {
        this.isCurrent = isCurrent;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Season(Integer id, Boolean isCurrent, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.isCurrent = isCurrent;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Season season = (Season) o;
        return id.equals(season.id) && isCurrent.equals(season.isCurrent) && name.equals(season.name) && startDate.equals(season.startDate) && endDate.equals(season.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isCurrent, name, startDate, endDate);
    }
}
