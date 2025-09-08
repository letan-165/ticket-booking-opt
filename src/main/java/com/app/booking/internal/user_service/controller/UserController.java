package com.app.booking.internal.user_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/public")
    ApiResponse<List<UserResponse>> findAll(@PageableDefault(size = 10) Pageable pageable){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.findAll(pageable).getContent())
                .build();
    }

    @GetMapping("/public/{id}")
    ApiResponse<UserResponse> findByID(@PathVariable String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.findById(id))
                .build();
    }

    @PatchMapping("/public/{id}")
    ApiResponse<UserResponse> update(@PathVariable String id,@RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.update(id,request))
                .build();
    }


}
