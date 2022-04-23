package by.bsuir.football.controller;

import by.bsuir.football.dto.user.CreateUserDto;
import by.bsuir.football.dto.user.UpdateUserDto;
import by.bsuir.football.service.UserService;
import by.bsuir.football.service.exceptions.user.DuplicateUsernameException;
import by.bsuir.football.service.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("createUserDto")
    public CreateUserDto createUserDto() {
        return new CreateUserDto();
    }

    @GetMapping("/registration")
    public String registrationForm() {
        return "user_registration";
    }

    @PostMapping("/registration")
    public String registerUser(@Valid CreateUserDto createUserDto, Errors errors, Model model) {
        if (errors.hasErrors()) return "user_registration";
        try {
            userService.save(createUserDto);
        } catch (DuplicateUsernameException ex) {
            model.addAttribute("duplicate_username_error", "Username " +
                            createUserDto.getUsername() + " is registered already");
            return "user_registration";
        }
        return "redirect:/login";
    }

    @PutMapping("/update")
    public String updateUser(@Valid UpdateUserDto updateUserDto, Errors errors) {
        if (errors.hasErrors()) return "";
        try {
            userService.update(updateUserDto);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}