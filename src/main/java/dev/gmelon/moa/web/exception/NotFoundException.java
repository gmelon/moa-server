package dev.gmelon.moa.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends MoaException {

    public NotFoundException(final String message, final Integer code) {
        super(HttpStatus.NOT_FOUND, message, code);
    }

    public NotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message, null);
    }

}
