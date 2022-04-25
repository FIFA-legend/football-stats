package by.bsuir.football.dto.league;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeagueDto {

    @Min(value = 0, message = "League id must not be negative")
    private Integer id;

    @NotEmpty(message = "League name must not be empty")
    private String name;

    @Min(value = 0, message = "Country id must not be negative")
    private Integer countryId;

}
