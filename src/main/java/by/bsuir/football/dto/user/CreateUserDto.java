package by.bsuir.football.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    @Size(min = 8, max = 32, message = "Username must be 8-32 characters long")
    private String username;

    @Size(min = 8, max = 32, message = "Password must be 8-32 characters long")
    private String password;

    @Email(message = "Email must be correct")
    private String email;

}
