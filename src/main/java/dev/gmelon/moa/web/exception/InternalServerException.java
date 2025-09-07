package dev.gmelon.moa.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerException extends MoaException {

    public InternalServerException(final String message, final Integer code) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, code);
    }

    public InternalServerException(final String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

}