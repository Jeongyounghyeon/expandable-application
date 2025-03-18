package com.study.app.repository;

import com.study.app.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE UserEntity u SET u.withdrawAt = NOW() WHERE u.id = :userId")
    void withdraw(@Param("userId") Long userId);
}
