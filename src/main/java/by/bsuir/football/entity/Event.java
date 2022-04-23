package by.bsuir.football.entity;

import by.bsuir.football.entity.enums.EventType;
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
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_home_team", nullable = false)
    private boolean isHomeTeam;

    @NotEmpty
    @Column(name = "minute", nullable = false)
    private String minute;

    @NotEmpty
    @Column(name = "footballer", nullable = false)
    private String footballer;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    public Event(Long id, boolean isHomeTeam, String minute, String footballer, EventType eventType) {
        this.id = id;
        this.isHomeTeam = isHomeTeam;
        this.minute = minute;
        this.footballer = footballer;
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return isHomeTeam == event.isHomeTeam && id.equals(event.id) && minute.equals(event.minute) && footballer.equals(event.footballer) && eventType == event.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isHomeTeam, minute, footballer, eventType);
    }
}