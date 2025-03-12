package com.study.expandable_application_auth.controller;

import com.study.expandable_application_auth.model.request.AuthenticationCreateRequest;
import com.study.expandable_application_auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Void> createAuthentication(@RequestBody AuthenticationCreateRequest request) {
        authenticationService.createAuthentication(request.getId(), request.getPassword(), request.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
