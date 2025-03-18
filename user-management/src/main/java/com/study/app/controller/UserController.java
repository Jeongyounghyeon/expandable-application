package com.study.app.controller;

import com.study.app.model.response.UserResponse;
import com.study.app.model.request.UserCreateRequest;
import com.study.app.model.request.UserUpdateRequest;
import com.study.app.service.AuthenticationService;
import com.study.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody UserCreateRequest request
    ) {
        userService.create(request.getUsername(), request.getId(), request.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<Void> update(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody UserUpdateRequest request,
            @PathVariable("user_id") Long userId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        userService.update(authenticatedId, userId, request.getUsername());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> withdraw(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable("user_id") Long userId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        userService.withdraw(authenticatedId, userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<UserResponse> detail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable("user_id") Long userId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        UserResponse responseBody = UserResponse.from(userService.detail(authenticatedId, userId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    private static String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
    }
}
