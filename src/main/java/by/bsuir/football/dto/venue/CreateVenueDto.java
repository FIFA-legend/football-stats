package by.bsuir.football.dto.venue;

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
public class CreateVenueDto {

    @NotEmpty(message = "Venue name must not be empty")
    private String name;

    @Min(value = 0, message = "Venue capacity must not be negative")
    private Integer capacity;

    @NotEmpty(message = "Venue city must not be empty")
    private String city;

    @Min(value = 0, message = "Country id must not be negative")
    private Integer countryId;

}
