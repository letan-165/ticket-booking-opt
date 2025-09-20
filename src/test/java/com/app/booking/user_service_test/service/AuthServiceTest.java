package com.app.booking.user_service_test.service;

import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.mapper.UserMapper;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.internal.user_service.service.AuthService;
import com.app.booking.user_service_test.model_mock.EntityMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthServiceTest {
    @Autowired
    @InjectMocks
    AuthService authService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;

    User user;
    String userID;
    UserResponse userResponse;

    @BeforeEach
    void initData(){
        user = EntityMock.userMock();
        userID = user.getId();
        userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        lenient().when(userMapper.toUserResponse(user)).thenReturn(userResponse);
    }






}
