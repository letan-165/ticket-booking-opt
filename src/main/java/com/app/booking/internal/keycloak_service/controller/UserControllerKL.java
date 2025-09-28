package com.app.booking.internal.keycloak_service.controller;

import com.app.booking.common.model.response.ApiResponse;
import com.app.booking.internal.keycloak_service.model.dto.request.UpdateUserRequest;
import com.app.booking.internal.keycloak_service.model.keycloak.UserKeycloak;
import com.app.booking.internal.keycloak_service.service.UserServiceKL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keycloak/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerKL {
    UserServiceKL userServiceKL;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public")
    public ApiResponse<List<UserKeycloak>> getUsers(@RequestParam(defaultValue = "0") int first,
                                                    @RequestParam(defaultValue = "10") int max) {
        return ApiResponse.<List<UserKeycloak>>builder()
                .result(userServiceKL.getUsers(first, max))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public/{username}")
    public ApiResponse<UserKeycloak> getUser(@PathVariable String username) {
        return ApiResponse.<UserKeycloak>builder()
                .result(userServiceKL.getUser(username))
                .build();
    }

    @PutMapping("/public")
    public ApiResponse<UserKeycloak> update(@RequestBody UpdateUserRequest request) {
        return ApiResponse.<UserKeycloak>builder()
                .result(userServiceKL.update(request))
                .build();
    }
}
