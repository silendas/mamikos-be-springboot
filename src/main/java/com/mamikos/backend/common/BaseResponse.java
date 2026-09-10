package com.mamikos.backend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> success(int code, String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> success(HttpStatus status, String message, T data) {
        return success(status.value(), message, data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return success(HttpStatus.OK.value(), message, data);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> BaseResponse<T> error(HttpStatus status, String message) {
        return error(status.value(), message);
    }

    public static <T> BaseResponse<T> error(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}

