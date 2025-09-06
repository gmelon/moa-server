package dev.gmelon.moa.web.service.user.dto;

import dev.gmelon.moa.storage.user.UserJoinType;
import dev.gmelon.moa.storage.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserResponse {

    private Long id;

    private String email;

    private String name;

    private UserRole role;

    private UserJoinType joinType;

}
