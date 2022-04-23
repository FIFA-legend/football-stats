package by.bsuir.football.dto.country;

import by.bsuir.football.entity.enums.Continent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCountryDto {

    private Integer id;

    private String name;

    private String code;

    private Continent continent;

}
