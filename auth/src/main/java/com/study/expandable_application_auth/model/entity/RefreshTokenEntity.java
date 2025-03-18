package com.study.expandable_application_auth.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_value", nullable = false)
    private String tokenValue;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    private RefreshTokenEntity(String tokenValue, Long userId, LocalDateTime expiredAt) {
        this.tokenValue = tokenValue;
        this.userId = userId;
        this.expiredAt = expiredAt;
    }

    public static RefreshTokenEntity of(String value, Long userId, LocalDateTime expiredAt) {
        return new RefreshTokenEntity(value, userId, expiredAt);
    }

    public void update(String tokenValue, LocalDateTime expiredAt) {
        this.tokenValue = tokenValue;
        this.expiredAt = expiredAt;
    }
}
