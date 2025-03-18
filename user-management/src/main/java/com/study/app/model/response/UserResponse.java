package com.study.app.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.app.model.dto.UserDto;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserResponse {

    @JsonProperty("id")
    Long id;

    @JsonProperty("username")
    String username;

    @JsonProperty("created_at")
    LocalDateTime createdAt;

    @JsonProperty("withdraw_at")
    LocalDateTime withdraw_at;

    public static UserResponse from(UserDto userDto) {
        return new UserResponse(
                userDto.getId(),
                userDto.getUsername(),
                userDto.getCreatedAt(),
                userDto.getWithdraw_at()
        );
    }
}
