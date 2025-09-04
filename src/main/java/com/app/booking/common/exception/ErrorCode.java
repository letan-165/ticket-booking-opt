package com.app.booking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PAYMENT_STATUS_INVALID(1011, "Payment status invalid (PENDING, SUCCESS, FAILED)", HttpStatus.BAD_REQUEST),
    SEAT_STATUS_INVALID(1010, "Seat status invalid (AVAILABLE, BOOKED, LOCKED)", HttpStatus.BAD_REQUEST),
    TICKER_STATUS_INVALID(1009, "Ticket status invalid (BOOKED, CANCELLED, CONFIRMED)", HttpStatus.BAD_REQUEST),
    ROLE_INVALID(1008, "User role invalid (USER, ORGANIZER, ADMIN)", HttpStatus.BAD_REQUEST),
    ENUM_INVALID(1007,"Enum invalid", HttpStatus.BAD_REQUEST),
    TOKEN_LOGOUT(1006,"Token had logout", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005,"Password don't valid", HttpStatus.BAD_REQUEST),
    AUTHENTICATION(1004,"Token not authentication ", HttpStatus.UNAUTHORIZED),
    AUTHORIZED(1003,"You don't have permission", HttpStatus.FORBIDDEN),
    USER_EXISTS(1002,"User existed", HttpStatus.BAD_REQUEST),
    USER_NO_EXISTS(1001,"User not exists", HttpStatus.BAD_REQUEST),
    OTHER_ERROL(9999,"Other errol", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode httpStatus;
}
