package com.app.booking.user_service_test.controller;

import com.app.booking.common.PageResponse;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.common.model_mock.ResponseMock;
import com.app.booking.internal.user_service.controller.UserController;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    UserRequest userRequest;
    UserResponse userResponse;
    String userID;

    @BeforeEach
    void initData() {
        userRequest = RequestMock.userMock();
        userResponse = ResponseMock.userMock();
        userID = userResponse.getId();
    }

    @Test
    void findAll_success() throws Exception {
        List<UserResponse> users = new ArrayList<>();
        users.add(userResponse);
        users.add(userResponse);
        users.add(userResponse);
        PageResponse<UserResponse> page = PageResponse.<UserResponse>builder()
                .content(users)
                .build();

        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/users/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void findByID_success() throws Exception {
        when(userService.findById(userID)).thenReturn(userResponse);

        mockMvc.perform(get("/users/public/{id}", userID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(userResponse.getId()))
                .andExpect(jsonPath("result.name").value(userResponse.getName()))
                .andExpect(jsonPath("result.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("result.role").value(userResponse.getRole().name()));

    }

    @Test
    void findByID_fail_USER_NO_EXISTS() throws Exception {
        ErrorCode errorCode = ErrorCode.USER_NO_EXISTS;

        when(userService.findById(userID)).thenThrow(new AppException(errorCode));

        mockMvc.perform(get("/users/public/{id}", userID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }

    @Test
    void update_success() throws Exception {
        var content = objectMapper.writeValueAsString(userRequest);

        when(userService.update(userID, userRequest)).thenReturn(userResponse);

        mockMvc.perform(patch("/users/public/{id}", userID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(userResponse.getId()))
                .andExpect(jsonPath("result.name").value(userResponse.getName()))
                .andExpect(jsonPath("result.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("result.role").value(userResponse.getRole().name()));
    }
}
