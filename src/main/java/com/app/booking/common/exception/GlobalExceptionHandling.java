package com.app.booking.common.exception;

import com.app.booking.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandling {

    ResponseEntity<ApiResponse> toResponseEntity(ErrorCode errorCode){
        return ResponseEntity.status(errorCode.httpStatus)
                .body(ApiResponse.builder()
                        .code(errorCode.code)
                        .message(errorCode.message)
                        .build());
    }
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(){
        return toResponseEntity(ErrorCode.OTHER_ERROL);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e){
        return toResponseEntity(e.getErrorCode());
    }

//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
//        Throwable cause = ex.getCause();
//
//        if (cause instanceof InvalidFormatException formatException) {
//            Class<?> targetType = formatException.getTargetType();
//
//            if (targetType != null && targetType.isEnum()) {
//                String enumName = targetType.getSimpleName();
//
//                return switch (enumName) {
//                    case "SupportStatus" -> toResponseEntity(ErrorCode.SUPPORT_INVALID);
//                    default -> toResponseEntity(ErrorCode.ENUM_INVALID);
//                };
//            }
//        }
//        return toResponseEntity(ErrorCode.ENUM_INVALID);
//    }




}
