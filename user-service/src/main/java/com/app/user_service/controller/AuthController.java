package com.app.user_service.controller;

import com.app.ticket_common_library.common.response.ApiResponse;
import com.app.user_service.model.dto.request.CreateUserRequest;
import com.app.user_service.model.dto.request.LoginRequest;
import com.app.user_service.model.dto.response.LoginResponse;
import com.app.user_service.model.keycloak.UserInfoKeyCloak;
import com.app.user_service.service.AuthService;
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
public class AuthController {
    AuthService authService;

    @PostMapping("/public/guest/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .result(authService.login(loginRequest))
                .build();
    }

    @PostMapping("/public/guest/register")
    public ApiResponse<String> createUser(@RequestBody CreateUserRequest request) {
        return ApiResponse.<String>builder()
                .result(authService.createUser(request))
                .build();
    }

    @GetMapping("/public/profile")
    public ApiResponse<UserInfoKeyCloak> profile() {
        return ApiResponse.<UserInfoKeyCloak>builder()
                .result(authService.userInfo())
                .build();
    }

    @GetMapping("/checkUserToken/userID")
    public ApiResponse<Boolean> checkUserToken(@PathVariable String userID) {
        authService.checkUserToken(userID);
        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }

}
