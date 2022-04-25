package by.bsuir.football.dto.venue;

import by.bsuir.football.dto.country.GetCountryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetVenueDto {

    private Integer id;

    private String name;

    private Integer capacity;

    private String city;

    private GetCountryDto country;

}
