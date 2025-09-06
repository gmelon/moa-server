package dev.gmelon.moa.web.config.resolver;

import dev.gmelon.moa.storage.user.User;
import dev.gmelon.moa.web.config.UserSessionHolder;
import dev.gmelon.moa.web.exception.NoPermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserSessionHolder userSessionHolder;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {
        if (!userSessionHolder.hasUser()) {
            throw new NoPermissionException("error.user.noPermission");
        }
        return userSessionHolder.getUser();
    }

}
