package by.bsuir.football.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStageDto {

    @NotEmpty(message = "Stage name must not be empty")
    private String name;

}
