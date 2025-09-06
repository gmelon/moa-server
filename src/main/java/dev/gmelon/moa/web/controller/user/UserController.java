package dev.gmelon.moa.web.controller.user;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.web.service.user.UserService;
import dev.gmelon.moa.web.service.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(User user) {
        return userService.getMe(user);
    }

}
