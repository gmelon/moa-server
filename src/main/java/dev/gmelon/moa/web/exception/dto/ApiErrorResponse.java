package dev.gmelon.moa.web.exception.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;

    @Nullable
    private Integer code;

    private String message;

    public static ApiErrorResponse of(int status, String message) {
        return ApiErrorResponse.builder()
            .status(status)
            .message(message)
            .build();
    }

    public static ApiErrorResponse of(int status, Integer code, String message) {
        return ApiErrorResponse.builder()
            .status(status)
            .code(code)
            .message(message)
            .build();
    }

}
