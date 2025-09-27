package com.app.booking.internal.keycloak_service.controller;

import com.app.booking.common.model.response.ApiResponse;
import com.app.booking.internal.keycloak_service.model.dto.request.UpdateUserRequest;
import com.app.booking.internal.keycloak_service.model.keycloak.UserKeycloak;
import com.app.booking.internal.keycloak_service.service.UserServiceKL;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keycloak/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerKL {
    UserServiceKL userServiceKL;

    String toToken(String auth) {
        return auth.replace("Bearer ", "");
    }

    @GetMapping("/public")
    public ApiResponse<List<UserKeycloak>> getUsers(@RequestHeader("Authorization") String token,
                                                    @RequestParam(required = false) String username,
                                                    @RequestParam(defaultValue = "0") int first,
                                                    @RequestParam(defaultValue = "10") int max) {
        return ApiResponse.<List<UserKeycloak>>builder()
                .result(userServiceKL.getUsers(toToken(token), username, first, max))
                .build();
    }

    @PutMapping("/public")
    public ApiResponse<String> update(@RequestHeader("Authorization") String token,
                                      @RequestBody UpdateUserRequest request) {
        return ApiResponse.<String>builder()
                .result(userServiceKL.update(toToken(token), request))
                .build();
    }
}
