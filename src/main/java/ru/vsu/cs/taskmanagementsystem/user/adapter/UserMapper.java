package ru.vsu.cs.taskmanagementsystem.user.adapter;

import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vsu.cs.taskmanagementsystem.security.entity.UserDetailsImpl;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.entity.User;
import ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse map(User user);

    User map(UserResponse userResponse);

    default UserDetails mapToUserDetails(User user) {
        return UserDetailsImpl.builder()
                .user(user)
                .build();
    }
}