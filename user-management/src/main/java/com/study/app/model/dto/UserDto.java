package com.study.app.model.dto;

import com.study.app.model.entity.UserEntity;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserDto {

    Long id;
    String username;
    LocalDateTime createdAt;
    LocalDateTime withdraw_at;

    public static UserDto from(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getCreatedAt(),
                userEntity.getWithdrawAt()
        );
    }
}
