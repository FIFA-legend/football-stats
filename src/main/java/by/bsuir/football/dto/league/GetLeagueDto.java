package by.bsuir.football.dto.league;

import by.bsuir.football.dto.country.GetCountryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetLeagueDto {

    private Integer id;

    private String name;

    private GetCountryDto country;

}
