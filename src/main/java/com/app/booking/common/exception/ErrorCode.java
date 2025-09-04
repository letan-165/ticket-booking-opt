package com.app.booking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ===== USER (1000 – 1999) =====
    USER_NO_EXISTS(1001, "User not exists", HttpStatus.BAD_REQUEST),
    USER_EXISTS(1002, "User existed", HttpStatus.BAD_REQUEST),
    ROLE_INVALID(1003, "User role invalid (USER, ORGANIZER, ADMIN)", HttpStatus.BAD_REQUEST),

    // ===== AUTH (2000 – 2999) =====
    UNAUTHORIZED(2001, "You don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(2002, "Token not authenticated", HttpStatus.UNAUTHORIZED),
    PASSWORD_INVALID(203, "Password invalid", HttpStatus.BAD_REQUEST),
    TOKEN_LOGOUT(2004, "Token had logout", HttpStatus.BAD_REQUEST),

    // ===== ENUM & VALIDATION (3000 – 3999) =====
    ENUM_INVALID(3001,"Enum invalid", HttpStatus.BAD_REQUEST),
    TICKET_STATUS_INVALID(3002, "Ticket status invalid (BOOKED, CANCELLED, CONFIRMED)", HttpStatus.BAD_REQUEST),
    SEAT_STATUS_INVALID(3003, "Seat status invalid (AVAILABLE, BOOKED, LOCKED)", HttpStatus.BAD_REQUEST),
    EVENT_NOT_FOUND(3004, "Event not found", HttpStatus.NOT_FOUND),
    PAYMENT_STATUS_INVALID(3004, "Payment status invalid (PENDING, SUCCESS, FAILED)", HttpStatus.BAD_REQUEST),


    // ===== COMMON (9000 – 9999) =====
    INTERNAL_ERROR(9000,"Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    OTHER_ERROR(9999,"Other error", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode httpStatus;
}
