package dev.gmelon.moa.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoPermissionException extends MoaException {

    public NoPermissionException(final String message, final Integer code) {
        super(HttpStatus.FORBIDDEN, message, code);
    }

    public NoPermissionException(final String message) {
        super(HttpStatus.FORBIDDEN, message, null);
    }

}
