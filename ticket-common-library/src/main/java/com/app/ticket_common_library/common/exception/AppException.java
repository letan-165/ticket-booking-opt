package com.app.ticket_common_library.common.exception;

import lombok.Data;

@Data
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
