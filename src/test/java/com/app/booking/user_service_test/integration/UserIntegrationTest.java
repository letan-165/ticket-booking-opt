package com.app.booking.user_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.PageResponse;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.common.model_mock.ResponseMock;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.user_service.controller.UserController;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.internal.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class UserIntegrationTest extends AbstractIntegrationTest {
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
        userRepository.deleteAllInBatch();
        for(int i = 0 ; i < 3 ;i++){
            User userMock = EntityMock.userMock();
            userMock.setId(null);
            userMock.setName(userMock.getName() + i);
            userMock.setEmail(i + userMock.getEmail());
            userMock.setPassword(passwordEncoder.encode(userMock.getPassword()));
            user = userRepository.save(userMock);
        }
    }

    @Test
    void findAll_success() throws Exception {
        mockMvc.perform(get("/users/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void findByID_success() throws Exception {
        mockMvc.perform(get("/users/public/{id}",user.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(user.getId()))
                .andExpect(jsonPath("result.name").value(user.getName()))
                .andExpect(jsonPath("result.email").value(user.getEmail()))
                .andExpect(jsonPath("result.role").value(user.getRole().name()));

    }

    @Test
    void findByID_fail_USER_NO_EXISTSs() throws Exception {
        ErrorCode errorCode = ErrorCode.USER_NO_EXISTS;
        mockMvc.perform(get("/users/public/{id}","fake_id")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(errorCode.getCode()))
                .andExpect(jsonPath("message").value(errorCode.getMessage()));

    }
    @Test
    void update_success() throws Exception {
        UserRequest request = UserRequest.builder()
                .name(user.getName() + "Update")
                .email(user.getEmail() + "Update")
                .build();

        mockMvc.perform(patch("/users/public/{id}",user.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(user.getId()))
                .andExpect(jsonPath("result.name").value(request.getName()))
                .andExpect(jsonPath("result.email").value(request.getEmail()))
                .andExpect(jsonPath("result.role").value(user.getRole().name()));
    }

}
