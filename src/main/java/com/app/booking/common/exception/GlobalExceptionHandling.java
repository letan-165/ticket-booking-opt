package com.app.booking.common.exception;

import com.app.booking.common.model.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandling {

    ResponseEntity<ApiResponse<Object>> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.httpStatus)
                .body(ApiResponse.builder()
                        .code(errorCode.code)
                        .message(errorCode.message)
                        .build());
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingException() {
        return toResponseEntity(ErrorCode.OTHER_ERROR);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Object>> handlingNoResourceFoundException() {
        return toResponseEntity(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException e) {
        return toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException formatException) {
            Class<?> targetType = formatException.getTargetType();

            if (targetType != null && targetType.isEnum()) {
                String enumName = targetType.getSimpleName();

                return switch (enumName) {
                    case "PaymentStatus" -> toResponseEntity(ErrorCode.PAYMENT_STATUS_INVALID);
                    case "SeatStatus" -> toResponseEntity(ErrorCode.SEAT_STATUS_INVALID);
                    case "TicketStatus" -> toResponseEntity(ErrorCode.TICKET_STATUS_INVALID);
                    case "UserRole" -> toResponseEntity(ErrorCode.ROLE_INVALID);
                    default -> toResponseEntity(ErrorCode.ENUM_INVALID);
                };
            }
        }
        return toResponseEntity(ErrorCode.ENUM_INVALID);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String field = Objects.requireNonNull(fieldError).getField();
        String annotation = fieldError.getCode();

        ErrorCode errorCode;
        switch (Objects.requireNonNull(annotation)) {
            case "NotBlank" -> errorCode = ErrorCode.NOT_BLANK;
            case "NotNull" -> errorCode = ErrorCode.NOT_NULL;
            case "Email" -> errorCode = ErrorCode.EMAIL_INVALID;
            default -> errorCode = ErrorCode.OTHER_ERROR;
        }

        String finalMessage = errorCode.getMessage().replace("{field}", field);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(finalMessage)
                        .build());
    }

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(WebClientResponseException ex) {
        return toResponseEntity(ErrorCode.UNAUTHORIZED_KEYCLOAK);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbidden(AuthorizationDeniedException ex) {
        return toResponseEntity(ErrorCode.UNAUTHORIZED);
    }

}
