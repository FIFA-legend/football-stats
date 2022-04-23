package by.bsuir.football.service;

import by.bsuir.football.dto.user.CreateUserDto;
import by.bsuir.football.dto.user.GetUserDto;
import by.bsuir.football.dto.user.UpdateUserDto;
import by.bsuir.football.entity.User;
import by.bsuir.football.entity.enums.Role;
import by.bsuir.football.service.exceptions.user.DuplicateUsernameException;
import by.bsuir.football.service.exceptions.user.UserNotFoundException;

import java.util.List;

public interface UserService {

    User get(Long id);

    User getUserByUsername(String username);

    User getGoogleUserByUsername(String username);

    List<GetUserDto> getByPage(int page, int count);

    void save(CreateUserDto createUserDto) throws DuplicateUsernameException;

    User saveGoogleUser(String username);

    void update(UpdateUserDto updateUserDto) throws UserNotFoundException;

    void updateRole(Long id, Role role) throws UserNotFoundException;

    void delete(Long id);

}
