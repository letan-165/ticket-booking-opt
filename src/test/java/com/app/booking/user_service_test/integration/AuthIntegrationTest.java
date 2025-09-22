package com.app.booking.user_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.user_service.dto.request.LoginRequest;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    VNPayService vnPayService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    User user;

    @BeforeEach
    void initData(){
        User userMock = EntityMock.userMock();
        userMock.setId(null);
        userMock.setPassword(passwordEncoder.encode(userMock.getPassword()));
        user = userRepository.save(userMock);
    }

    @Test
    void register_success() throws Exception {
        UserRequest request = RequestMock.userMock();
        request.setEmail("success@email");

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000));

        assertTrue(userRepository.existsByEmail(request.getEmail()));
    }
    @Test
    void register_fail_USER_EXISTS() throws Exception {
        UserRequest request = RequestMock.userMock();
        ErrorCode errorCode = ErrorCode.USER_EXISTS;

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }


    @Test
    void login_success() throws Exception {
        User userMock = EntityMock.userMock();
        LoginRequest request = LoginRequest.builder()
                .email(userMock.getEmail())
                .password(userMock.getPassword())
                .build();

        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.userID").value(user.getId()))
                .andExpect(jsonPath("result.name").value(user.getName()))
                .andExpect(jsonPath("result.token",not(emptyOrNullString())));
    }

    @Test
    void login_fail_USER_NO_EXISTS() throws Exception {
        User userMock = EntityMock.userMock();
        LoginRequest request = LoginRequest.builder()
                .email("fake_email@email")
                .password(userMock.getPassword())
                .build();

        ErrorCode errorCode = ErrorCode.USER_NO_EXISTS;
        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }

    @Test
    void login_fail_PASSWORD_INVALID() throws Exception {
        User userMock = EntityMock.userMock();
        LoginRequest request = LoginRequest.builder()
                .email(userMock.getEmail())
                .password("fake password")
                .build();

        ErrorCode errorCode = ErrorCode.PASSWORD_INVALID;
        mockMvc.perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));
    }




}
