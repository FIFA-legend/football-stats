package by.bsuir.football.dto.country;

import by.bsuir.football.entity.enums.Continent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCountryDto {

    @Min(value = 0, message = "Country id must not be negative")
    private Integer id;

    @Pattern(regexp = "[A-Z][A-Za-z]+", message = "Name must start from capital letter and contain letters only")
    private String name;

    @Pattern(regexp = "[a-z]{2}", message = "Code must contain 2 lowercase letters")
    private String code;

    @NotNull(message = "Continent must not be null")
    private Continent continent;

}
