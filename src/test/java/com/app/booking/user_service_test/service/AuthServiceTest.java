package com.app.booking.user_service_test.service;

import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.user_service.dto.request.LoginRequest;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.LoginResponse;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.mapper.UserMapper;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.internal.user_service.service.AuthService;
import com.app.booking.model_mock.EntityMock;
import com.app.booking.model_mock.RequestMock;
import com.app.booking.model_mock.ResponseMock;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthServiceTest {
    @Spy
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

    @BeforeEach
    void initData(){
        user = EntityMock.userMock();
    }

    @Test
    void register_success(){
        UserRequest request = RequestMock.userMock();
        UserResponse userResponse = ResponseMock.userMock();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = authService.register(request);

        verify(passwordEncoder,times(1)).encode(eq(request.getPassword()));

        assertThat(response).isEqualTo(userResponse);
    }

    @Test
    void register_fail_USER_EXISTS(){
        UserRequest request = RequestMock.userMock();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        var exception = assertThrows(AppException.class,
                ()-> authService.register(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EXISTS);
    }

    @Test
    void login_success() throws JOSEException {
        LoginRequest request = RequestMock.loginMock();
        LoginResponse loginResponse = ResponseMock.loginMock();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        doReturn(loginResponse.getToken()).when(authService).generate(user);

        LoginResponse response = authService.login(request);

        assertThat(response).isEqualTo(loginResponse);
    }

    @Test
    void login_fail_USER_NO_EXISTS() throws JOSEException {
        LoginRequest request = RequestMock.loginMock();
        LoginResponse loginResponse = ResponseMock.loginMock();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class,
                ()-> authService.login(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NO_EXISTS);
    }

    @Test
    void login_fail_PASSWORD_INVALID() throws JOSEException {
        LoginRequest request = RequestMock.loginMock();
        LoginResponse loginResponse = ResponseMock.loginMock();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
        var exception = assertThrows(AppException.class,
                ()-> authService.login(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_INVALID);

    }






}
