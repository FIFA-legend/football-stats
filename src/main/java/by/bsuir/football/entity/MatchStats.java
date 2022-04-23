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
import javax.validation.constraints.Min;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "match_stats")
public class MatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    @Column(name = "half_time_home_team_score")
    private Integer halfTimeHomeTeamScore;

    @Min(0)
    @Column(name = "half_time_away_team_score")
    private Integer halfTimeAwayTeamScore;

    @Min(0)
    @Column(name = "full_time_home_team_score")
    private Integer fullTimeHomeTeamScore;

    @Min(0)
    @Column(name = "full_time_away_team_score")
    private Integer fullTimeAwayTeamScore;

    @Min(0)
    @Column(name = "extra_time_home_team_score")
    private Integer extraTimeHomeTeamScore;

    @Min(0)
    @Column(name = "extra_time_away_team_score")
    private Integer extraTimeAwayTeamScore;

    @Min(0)
    @Column(name = "penalty_home_team_score")
    private Integer penaltyHomeTeamScore;

    @Min(0)
    @Column(name = "penalty_away_team_score")
    private Integer penaltyAwayTeamScore;

    @Min(0)
    @Column(name = "attendance")
    private Integer attendance;

    public MatchStats(Long id, Integer halfTimeHomeTeamScore, Integer halfTimeAwayTeamScore, Integer fullTimeHomeTeamScore,
                      Integer fullTimeAwayTeamScore, Integer extraTimeHomeTeamScore, Integer extraTimeAwayTeamScore,
                      Integer penaltyHomeTeamScore, Integer penaltyAwayTeamScore, Integer attendance) {
        this.id = id;
        this.halfTimeHomeTeamScore = halfTimeHomeTeamScore;
        this.halfTimeAwayTeamScore = halfTimeAwayTeamScore;
        this.fullTimeHomeTeamScore = fullTimeHomeTeamScore;
        this.fullTimeAwayTeamScore = fullTimeAwayTeamScore;
        this.extraTimeHomeTeamScore = extraTimeHomeTeamScore;
        this.extraTimeAwayTeamScore = extraTimeAwayTeamScore;
        this.penaltyHomeTeamScore = penaltyHomeTeamScore;
        this.penaltyAwayTeamScore = penaltyAwayTeamScore;
        this.attendance = attendance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchStats that = (MatchStats) o;
        return id.equals(that.id) && Objects.equals(halfTimeHomeTeamScore, that.halfTimeHomeTeamScore) &&
                Objects.equals(halfTimeAwayTeamScore, that.halfTimeAwayTeamScore) && Objects.equals(fullTimeHomeTeamScore, that.fullTimeHomeTeamScore) &&
                Objects.equals(fullTimeAwayTeamScore, that.fullTimeAwayTeamScore) && Objects.equals(extraTimeHomeTeamScore, that.extraTimeHomeTeamScore) &&
                Objects.equals(extraTimeAwayTeamScore, that.extraTimeAwayTeamScore) && Objects.equals(penaltyHomeTeamScore, that.penaltyHomeTeamScore) &&
                Objects.equals(penaltyAwayTeamScore, that.penaltyAwayTeamScore) && Objects.equals(attendance, that.attendance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, halfTimeHomeTeamScore, halfTimeAwayTeamScore, fullTimeHomeTeamScore, fullTimeAwayTeamScore, extraTimeHomeTeamScore, extraTimeAwayTeamScore, penaltyHomeTeamScore, penaltyAwayTeamScore, attendance);
    }
}
