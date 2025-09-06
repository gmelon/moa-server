package dev.gmelon.moa.web.config;

import dev.gmelon.moa.storage.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Setter
@Getter
@RequestScope
@Component
public class UserSessionHolder {

    private User user;

    public boolean hasUser() {
        return user != null;
    }

}
