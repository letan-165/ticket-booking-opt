package com.app.booking.internal.user_service.mapper;

import com.app.booking.internal.user_service.dto.request.UserRequest;
import com.app.booking.internal.user_service.dto.response.UserResponse;
import com.app.booking.internal.user_service.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    User toUser(UserRequest request);
    UserResponse toUserResponse(User user);
    void updateUserFromRequest(@MappingTarget User user,UserRequest request);
}
