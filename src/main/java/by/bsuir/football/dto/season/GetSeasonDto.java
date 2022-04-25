package by.bsuir.football.dto.season;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSeasonDto {

    private Integer id;

    private Boolean isCurrent;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

}
