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
        if (statusCode.is4xxClientError()) {
            if (exception instanceof MoaException moaException) {
                return ResponseEntity
                        .status(statusCode)
                        .body(ApiErrorResponse.of(
                                statusCode.value(),
                                moaException.getCode(),
                                messageSource.getMessage(moaException.getReason(), null, getLocale())
                        ));
            }
            return ResponseEntity.status(statusCode)
                    .body(ApiErrorResponse.of(statusCode.value(), exception.getLocalizedMessage()));
        }

        log.error("Unexpected error occurred: {}", exception.getClass().getSimpleName(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage("error.web.internalServerError", null, getLocale())
        ));
    }

}
