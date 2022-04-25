package by.bsuir.football.entity;

import by.bsuir.football.entity.enums.Formation;
import by.bsuir.football.entity.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Season season;

    @NotNull
    @ManyToOne
    private League league;

    @NotNull
    @ManyToOne
    private Stage stage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @ManyToOne
    private Team homeTeam;

    @NotNull
    @ManyToOne
    private Team awayTeam;

    @NotNull
    @ManyToOne
    private Venue venue;

    @NotNull
    @ManyToOne
    private Referee referee;

    @NotNull
    @ManyToOne
    private MatchStats matchStats;

    @Enumerated(EnumType.STRING)
    @Column(name = "home_team_formation", nullable = false)
    private Formation homeTeamFormation;

    @Enumerated(EnumType.STRING)
    @Column(name = "away_team_formation", nullable = false)
    private Formation awayTeamFormation;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Event> events;

    public Match(Long id, Season season, League league, Stage stage, Status status, LocalDate startDate,
                 LocalTime startTime, Team homeTeam, Team awayTeam, Venue venue, Referee referee, MatchStats matchStats,
                 Formation homeTeamFormation, Formation awayTeamFormation, Set<Event> events) {
        this.id = id;
        this.season = season;
        this.league = league;
        this.stage = stage;
        this.status = status;
        this.startDate = startDate;
        this.startTime = startTime;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.venue = venue;
        this.referee = referee;
        this.matchStats = matchStats;
        this.homeTeamFormation = homeTeamFormation;
        this.awayTeamFormation = awayTeamFormation;
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return id.equals(match.id) && season.equals(match.season) && league.equals(match.league) && stage.equals(match.stage) &&
                status == match.status && startDate.equals(match.startDate) && startTime.equals(match.startTime) &&
                homeTeam.equals(match.homeTeam) && awayTeam.equals(match.awayTeam) && venue.equals(match.venue) &&
                referee.equals(match.referee) && matchStats.equals(match.matchStats) && homeTeamFormation == match.homeTeamFormation &&
                awayTeamFormation == match.awayTeamFormation && events.equals(match.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, season, league, stage, status, startDate, startTime, homeTeam, awayTeam, venue, referee, matchStats, homeTeamFormation, awayTeamFormation, events);
    }
}