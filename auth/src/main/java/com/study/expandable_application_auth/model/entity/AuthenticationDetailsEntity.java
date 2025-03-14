package com.study.expandable_application_auth.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "authentication_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class AuthenticationDetailsEntity {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
}
