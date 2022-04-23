package by.bsuir.football.dto.user;

import by.bsuir.football.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserDto {

    private Long id;

    private String username;

    private String email;

    private Role role;

}
