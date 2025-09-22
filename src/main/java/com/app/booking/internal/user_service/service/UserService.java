package com.app.booking.internal.user_service.service;

import com.app.booking.common.PageResponse;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import com.app.booking.internal.user_service.mapper.UserMapper;
import com.app.booking.internal.user_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public User findUser(String userID){
        return userRepository.findById(userID)
                .orElseThrow(()->new AppException(ErrorCode.USER_NO_EXISTS));
    }
    public void userIsExist(String userID){
        if(!userRepository.existsById(userID))
            throw new AppException(ErrorCode.USER_NO_EXISTS);
    }

    @Cacheable(value = "users", keyGenerator = "pageableKeyGenerator")
    public PageResponse<UserResponse> findAll(Pageable pageable) {
        Page<UserResponse> page = userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Cacheable(value = "user", keyGenerator  = "simpleKeyGenerator")
    public UserResponse findById(String id) {
        User user = findUser(id);
        return userMapper.toUserResponse(user);
    }

    @CachePut(value="user", key="'findById:' + #id")
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse update(String id,@Valid UserRequest request) {
        User user = findUser(id);
        userMapper.updateUserFromRequest(user,request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
