package by.bsuir.football.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamDto {

    @Min(value = 0, message = "Team id must not be negative")
    private Integer id;

    @NotEmpty(message = "Team name must not be empty")
    private String fullName;

    @Pattern(regexp = "[A-Z]{3}", message = "Team short name must match regexp XXX")
    private String shortName;

    @Min(value = 0, message = "Country id must not be negative")
    private Integer countryId;

    private boolean hasLogo;

}
