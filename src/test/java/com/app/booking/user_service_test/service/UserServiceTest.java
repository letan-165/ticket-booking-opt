package com.app.booking.user_service_test.service;

import com.app.booking.common.enums.UserRole;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.mapper.UserMapper;
import com.app.booking.internal.user_service.repository.UserRepository;
import com.app.booking.internal.user_service.service.UserService;
import com.app.booking.user_service_test.model_mock.EntityMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceTest {
    @Autowired
    @InjectMocks
    UserService userService;

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

    @Test
    void findById_success(){
        when(userRepository.findById(userID)).thenReturn(Optional.ofNullable(user));

        UserResponse userRes = userService.findById(userID);

        assertThat(userRes.getId()).isEqualTo(user.getId());
        assertThat(userRes.getName()).isEqualTo(user.getName());
        assertThat(userRes.getEmail()).isEqualTo(user.getEmail());
        assertThat(userRes.getRole()).isEqualTo(user.getRole());
    }

    @Test
    void findById_fail_USER_NO_EXISTS(){
        when(userRepository.findById(userID)).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class,
                ()-> userService.findById(userID));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NO_EXISTS);
    }

    @Test
    void update_success_set_password(){
        UserRequest request = UserRequest.builder()
                .name("name")
                .email("email")
                .password("1")
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse userRes = userService.update(userID, request);

        verify(userMapper).updateUserFromRequest(eq(user), eq(request));
        verify(passwordEncoder,times(1)).encode(eq(request.getPassword()));

        assertThat(userRes.getId()).isEqualTo(user.getId());
        assertThat(userRes.getName()).isEqualTo(user.getName());
        assertThat(userRes.getEmail()).isEqualTo(user.getEmail());
        assertThat(userRes.getRole()).isEqualTo(user.getRole());
    }

    @Test
    void update_success_noSet_password(){
        UserRequest request = UserRequest.builder()
                .name("name")
                .email("email")
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse userRes = userService.update(userID, request);

        verify(userMapper).updateUserFromRequest(eq(user), eq(request));
        verify(passwordEncoder,times(0)).encode(eq(request.getPassword()));

        assertThat(userRes.getId()).isEqualTo(user.getId());
        assertThat(userRes.getName()).isEqualTo(user.getName());
        assertThat(userRes.getEmail()).isEqualTo(user.getEmail());
        assertThat(userRes.getRole()).isEqualTo(user.getRole());
    }
}
