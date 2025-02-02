package dev.gmelon.moa.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public abstract class MoaException extends ResponseStatusException {

    private final Integer code;

    public MoaException(final HttpStatus status, final String message, final Integer code) {
        super(status, message);
        this.code = code;
    }

}
