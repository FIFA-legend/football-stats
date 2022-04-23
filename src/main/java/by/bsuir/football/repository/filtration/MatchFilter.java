package by.bsuir.football.repository.filtration;

import by.bsuir.football.entity.League;
import by.bsuir.football.entity.Season;
import by.bsuir.football.entity.Stage;
import by.bsuir.football.entity.Team;
import by.bsuir.football.entity.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MatchFilter {

    private Season season;

    private League league;

    private Stage stage;

    private Status status;

    private LocalDate startPeriod;

    private LocalDate endPeriod;

    private Team team1;

    private Team team2;

    public MatchFilter(Season season, League league, Stage stage, Status status, LocalDate startPeriod, LocalDate endPeriod, Team team1, Team team2) {
        this.season = season;
        this.league = league;
        this.stage = stage;
        this.status = status;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchFilter that = (MatchFilter) o;
        return Objects.equals(season, that.season) && Objects.equals(league, that.league) && Objects.equals(stage, that.stage) && status == that.status && Objects.equals(startPeriod, that.startPeriod) && Objects.equals(endPeriod, that.endPeriod) && Objects.equals(team1, that.team1) && Objects.equals(team2, that.team2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(season, league, stage, status, startPeriod, endPeriod, team1, team2);
    }
}
