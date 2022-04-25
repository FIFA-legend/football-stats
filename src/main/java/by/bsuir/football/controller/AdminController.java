package by.bsuir.football.controller;

import by.bsuir.football.dto.user.GetUserDto;
import by.bsuir.football.service.UserService;
import by.bsuir.football.service.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    private final int USERS_COUNT = 10;

    private final String USER_RETURN_URL = "/queue/admin/users/return";

    private final UserService userService;

    private final SimpMessagingTemplate messaging;

    private final Map<String, Integer> map;

    @Autowired
    public AdminController(UserService userService, SimpMessagingTemplate messaging) {
        this.userService = userService;
        this.messaging = messaging;
        map = new HashMap<>();
    }

    @GetMapping("/admin")
    public String adminPage(Principal principal) {
        String username = principal.getName();
        map.put(username, 0);
        return "admin_page";
    }

    @MessageMapping("/admin/users")
    public void sendUsersOfFirstPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        List<GetUserDto> users = userService.getByPage(page, USERS_COUNT);
        messaging.convertAndSendToUser(username, USER_RETURN_URL, convertListToArray(users));
    }

    @MessageMapping("/admin/users/next")
    public void sendUsersOfNextPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) + 1;
        List<GetUserDto> users = userService.getByPage(page, USERS_COUNT);
        if (users.isEmpty()) {
            page = page - 1;
            users = userService.getByPage(page, USERS_COUNT);
        }
        map.put(username, page);
        messaging.convertAndSendToUser(username, USER_RETURN_URL, convertListToArray(users));
    }

    @MessageMapping("/admin/users/previous")
    public void sendUsersOfPreviousPage(Principal principal) {
        String username = principal.getName();
        int page = map.get(username) == 0 ? 0 : map.get(username) - 1;
        List<GetUserDto> users = userService.getByPage(page, USERS_COUNT);
        map.put(username, page);
        messaging.convertAndSendToUser(username, USER_RETURN_URL, convertListToArray(users));
    }

    @MessageMapping("/admin/users/delete/{id}")
    public void deleteUser(@DestinationVariable Long id, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        userService.delete(id);
        List<GetUserDto> users = userService.getByPage(page, USERS_COUNT);
        messaging.convertAndSendToUser(username, USER_RETURN_URL, convertListToArray(users));
    }

    @MessageMapping("/admin/users/update/{id}")
    public void updateUserRole(@DestinationVariable Long id, GetUserDto user, Principal principal) {
        String username = principal.getName();
        int page = map.get(username);
        try {
            userService.updateRole(id, user.getRole());
        } catch (UserNotFoundException ex) {
            ex.printStackTrace();
        }

        List<GetUserDto> users = userService.getByPage(page, USERS_COUNT);
        messaging.convertAndSendToUser(username, USER_RETURN_URL, convertListToArray(users));
    }

    private GetUserDto[] convertListToArray(List<GetUserDto> list) {
        GetUserDto[] array = new GetUserDto[list.size()];
        return list.toArray(array);
    }

}