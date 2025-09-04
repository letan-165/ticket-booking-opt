package com.app.booking.internal.user_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.internal.user_service.dto.request.LoginRequest;
import com.app.booking.internal.user_service.dto.request.TokenRequest;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.LoginResponse;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.service.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/public/register")
    ApiResponse<UserResponse> register(@Valid @RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/public/login")
    ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) throws JOSEException {
        return ApiResponse.<LoginResponse>builder()
                .result(authService.login(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<Boolean> introspect(@RequestBody TokenRequest request) throws ParseException, JOSEException {
        return ApiResponse.<Boolean>builder()
                .result(authService.introspect(request))
                .build();
    }

}
