package dev.gmelon.moa.web.config;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import dev.gmelon.moa.web.exception.MoaException;
import dev.gmelon.moa.web.exception.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class MoaExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception exception,
            final Object body,
            final HttpHeaders headers,
            final HttpStatusCode statusCode,
            final WebRequest request
    ) {
        if (exception instanceof MoaException moaException) {
            return handleMoaException(moaException, statusCode);
        }
        
        if (statusCode.is4xxClientError()) {
            return ResponseEntity.status(statusCode)
                    .body(ApiErrorResponse.of(statusCode.value(), exception.getLocalizedMessage()));
        }

        return handleServerError(exception);
    }

    private ResponseEntity<Object> handleMoaException(MoaException exception, HttpStatusCode statusCode) {
        if (statusCode.is4xxClientError()) {
            return ResponseEntity
                    .status(statusCode)
                    .body(ApiErrorResponse.of(
                            statusCode.value(),
                            exception.getCode(),
                            messageSource.getMessage(exception.getReason(), null, getLocale())
                    ));
        }
        
        return handleServerError(exception);
    }

    private ResponseEntity<Object> handleServerError(Exception exception) {
        log.error("Server error occurred: {}", exception.getClass().getSimpleName(), exception);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage("error.web.internalServerError", null, getLocale())
        ));
    }

}
