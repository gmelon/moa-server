package dev.gmelon.moa.web.service.user;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.web.service.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    public UserResponse getMe(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .joinType(user.getJoinType())
                .build();
    }

}
