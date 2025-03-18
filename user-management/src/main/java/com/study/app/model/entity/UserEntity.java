package com.study.app.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@SQLRestriction("withdraw_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;

    private UserEntity(String username) {
        this.username = username;
    }

    public static UserEntity of(String username) {
        return new UserEntity(username);
    }

    public void update(String username) {
        this.username = username;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
