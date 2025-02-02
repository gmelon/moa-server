package dev.gmelon.moa.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends MoaException {

    public BadRequestException(final String message, final Integer code) {
        super(HttpStatus.BAD_REQUEST, message, code);
    }

    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message, null);
    }

}
