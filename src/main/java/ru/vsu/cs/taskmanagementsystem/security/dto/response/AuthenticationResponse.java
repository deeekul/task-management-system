package ru.vsu.cs.taskmanagementsystem.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthenticationResponse(

        @JsonProperty("access_token")
        @Schema(description = "Access Token")
        String accessToken,

        @JsonProperty("refresh_token")
        @Schema(description = "Refresh Token")
        String refreshToken
) {
}