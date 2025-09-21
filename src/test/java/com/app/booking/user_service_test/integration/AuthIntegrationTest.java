package com.app.booking.user_service_test.integration;

import com.app.booking.common.AbstractIntegrationTest;
import com.app.booking.common.enums.UserRole;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Test
    void register_Success() throws Exception {
        UserRequest req = UserRequest.builder()
                .name("dev")
                .email("dev@example.com")
                .password("123456")
                .role(UserRole.USER)
                .build();

        mockMvc.perform(post("/auth/public/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000));

        assertTrue(userRepository.existsByEmail("dev@example.com"));
    }
}
