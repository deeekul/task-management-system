package ru.vsu.cs.taskmanagementsystem.user.adapter.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.vsu.cs.taskmanagementsystem.security.entity.Role;

@Builder
public record UserResponse(

        Long id,

        String firstName,

        String lastName,

        String login,

        Role role
) {
}