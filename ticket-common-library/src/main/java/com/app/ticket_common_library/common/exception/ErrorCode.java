package com.app.ticket_common_library.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ===== SERVICE (1001 – 1999) =====
    // === USER ===
    USER_NO_EXISTS(1001, "User not found", HttpStatus.BAD_REQUEST),
    USER_EXISTS(1002, "User existed", HttpStatus.BAD_REQUEST),
    EVENT_NO_EXISTS(1003, "Event not found", HttpStatus.BAD_REQUEST),
    SEAT_NO_EXISTS(1004, "Seat not found", HttpStatus.BAD_REQUEST),
    TICKET_NO_EXISTS(1005, "Ticket not found", HttpStatus.BAD_REQUEST),
    TICKET_NO_AVAILABLE(1006, "Cannot initiate ticket when seat status is invalid", HttpStatus.BAD_REQUEST),
    PRICE_EVENT_INVALID(1007, "Invalid event price", HttpStatus.BAD_REQUEST),
    PAYMENT_NO_EXISTS(1008, "Payment not found", HttpStatus.BAD_REQUEST),
    PAYMENT_NO_PENDING(1009, "Cannot payment when status is invalid", HttpStatus.BAD_REQUEST),
    PAYMENT_FAIL(1010, "Payment fail", HttpStatus.BAD_REQUEST),
    USER_INVALID(1011, "User does not match token", HttpStatus.BAD_REQUEST),

    // ===== AUTH (2001 – 2999) =====
    UNAUTHORIZED(2001, "You don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(2002, "Token not authenticated", HttpStatus.UNAUTHORIZED),
    PASSWORD_INVALID(203, "Password invalid", HttpStatus.BAD_REQUEST),
    TOKEN_LOGOUT(2004, "Token had logout", HttpStatus.BAD_REQUEST),
    PARSE_TOKEN_FAIL(2005, "Jwt invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_KEYCLOAK(2006, "You don't have permission access keycloak", HttpStatus.FORBIDDEN),


    // ===== ENUM & VALIDATION (3001 – 3999) =====
    // ===== ENUM (3001 – 3099) =====
    ENUM_INVALID(3001, "Enum invalid", HttpStatus.BAD_REQUEST),
    TICKET_STATUS_INVALID(3002, "Ticket status invalid (BOOKED, CANCELLED, CONFIRMED)", HttpStatus.BAD_REQUEST),
    SEAT_STATUS_INVALID(3003, "Seat status invalid (AVAILABLE, BOOKED, LOCKED)", HttpStatus.BAD_REQUEST),
    PAYMENT_STATUS_INVALID(3004, "Payment status invalid (PENDING, SUCCESS, FAILED)", HttpStatus.BAD_REQUEST),
    ROLE_INVALID(3005, "User role invalid (USER, ORGANIZER, ADMIN)", HttpStatus.BAD_REQUEST),
    // === VALIDATION (3101 – 3999) ===
    EMAIL_INVALID(3101, "Email invalid", HttpStatus.BAD_REQUEST),
    NOT_BLANK(3102, "Value request.{field} is blank", HttpStatus.BAD_REQUEST),
    NOT_NULL(3103, "Value request.{field} is null", HttpStatus.BAD_REQUEST),

    // ===== COMMON (9000 – 9999) =====
    INTERNAL_ERROR(9001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND(9002, "Not Found", HttpStatus.INTERNAL_SERVER_ERROR),
    OTHER_ERROR(9999, "Other error", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode httpStatus;
}
