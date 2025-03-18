package com.study.expandable_application_auth.repository;

import com.study.expandable_application_auth.model.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {

    Optional<RefreshTokenEntity> findByTokenValue(String tokenValue);
}
