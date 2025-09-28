package com.app.booking.internal.keycloak_service.controller;

import com.app.booking.common.model.response.ApiResponse;
import com.app.booking.internal.keycloak_service.model.dto.request.CreateUserRequest;
import com.app.booking.internal.keycloak_service.model.dto.request.LoginRequest;
import com.app.booking.internal.keycloak_service.model.dto.response.LoginResponse;
import com.app.booking.internal.keycloak_service.model.keycloak.UserInfoKeyCloak;
import com.app.booking.internal.keycloak_service.service.AuthServiceKL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keycloak/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthControllerKL {
    AuthServiceKL authServiceKL;

    @PostMapping("/public/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .result(authServiceKL.login(loginRequest))
                .build();
    }

    @PostMapping("/public/register")
    public ApiResponse<String> createUser(@RequestBody CreateUserRequest request) {
        return ApiResponse.<String>builder()
                .result(authServiceKL.createUser(request))
                .build();
    }

    @GetMapping("/public/profile")
    public ApiResponse<UserInfoKeyCloak> profile() {
        return ApiResponse.<UserInfoKeyCloak>builder()
                .result(authServiceKL.userInfo())
                .build();
    }

}
