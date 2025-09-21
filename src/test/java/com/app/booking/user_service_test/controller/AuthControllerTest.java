package com.app.booking.user_service_test.controller;

import com.app.booking.common.enums.UserRole;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.user_service.controller.AuthController;
import com.app.booking.internal.user_service.dto.request.LoginRequest;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.LoginResponse;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.service.AuthService;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.common.model_mock.ResponseMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @Test
    void register_success() throws Exception {
        UserRequest request = RequestMock.userMock();
        UserResponse response = ResponseMock.userMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(response.getId()))
                .andExpect(jsonPath("result.name").value(response.getName()))
                .andExpect(jsonPath("result.email").value(response.getEmail()))
                .andExpect(jsonPath("result.role").value(response.getRole().name()));
    }

    @Test
    void register_fail_unValid_email() throws Exception {

        ErrorCode errorCode = ErrorCode.EMAIL_INVALID;
        UserRequest request = UserRequest.builder()
                .name("name")
                .password("123")
                .email("email")
                .role(UserRole.USER)
                .build();
        UserResponse response = ResponseMock.userMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }

    @Test
    void register_fail_blank() throws Exception {
        ErrorCode errorCode = ErrorCode.NOT_BLANK;
        UserRequest request = UserRequest.builder()
                .name("")
                .password("123")
                .email("email@email")
                .role(UserRole.USER)
                .build();
        UserResponse response = ResponseMock.userMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.register(request)).thenReturn(response);
        String finalMessage = errorCode.getMessage().replace("{field}", "name");

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(finalMessage));

    }

    @Test
    void login_success() throws Exception {
        LoginRequest request = RequestMock.loginMock();
        LoginResponse response = ResponseMock.loginMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.userID").value(response.getUserID()))
                .andExpect(jsonPath("result.name").value(response.getName()))
                .andExpect(jsonPath("result.token").value(response.getToken()));
    }

    @Test
    void login_fail_unValid_email() throws Exception {
        ErrorCode errorCode = ErrorCode.EMAIL_INVALID;
        LoginRequest request = LoginRequest.builder()
                .email("email")
                .password("123")
                .build();
        LoginResponse response = ResponseMock.loginMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }

    @Test
    void login_fail_blank() throws Exception {
        ErrorCode errorCode = ErrorCode.NOT_BLANK;
        LoginRequest request = LoginRequest.builder()
                .email("email@email")
                .password("")
                .build();
        LoginResponse response = ResponseMock.loginMock();

        var content = objectMapper.writeValueAsString(request);

        when(authService.login(request)).thenReturn(response);
        String finalMessage = errorCode.getMessage().replace("{field}", "password");

        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(finalMessage));

    }



}
