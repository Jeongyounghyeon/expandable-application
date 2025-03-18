package com.study.expandable_application_auth.repository;

import com.study.expandable_application_auth.model.entity.AuthenticationDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationDetailsRepository extends JpaRepository<AuthenticationDetailsEntity, String> {
}
