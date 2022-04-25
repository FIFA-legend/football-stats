package by.bsuir.football.dto.team;

import by.bsuir.football.dto.country.GetCountryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTeamDto {

    private Integer id;

    private String fullName;

    private String shortName;

    private GetCountryDto country;

    private boolean hasLogo;

}
