package com.study.expandable_application_auth.controller;

import com.study.expandable_application_auth.exception.ExceptionCode;
import com.study.expandable_application_auth.exception.ExpandableException;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import com.study.expandable_application_auth.model.request.AuthenticateRequest;
import com.study.expandable_application_auth.model.request.AuthenticationCreateRequest;
import com.study.expandable_application_auth.model.response.AuthenticateResponse;
import com.study.expandable_application_auth.model.response.RefreshResponse;
import com.study.expandable_application_auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationService authenticationService;

    @GetMapping("/exist/{id}")
    public ResponseEntity<Boolean> isExistId(@PathVariable String id) {
        return ResponseEntity.ok(authenticationService.isExistId(id));
    }

    @PostMapping
    public ResponseEntity<Void> createAuthentication(@RequestBody AuthenticationCreateRequest request) {
        authenticationService.createAuthentication(request.getId(), request.getPassword(), request.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest request) {
        JwtTokenDto jwtTokenDto = authenticationService.authenticate(request.getId(), request.getPassword());

        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthenticateResponse.from(jwtTokenDto));
    }

    @GetMapping("/access")
    public ResponseEntity<Void> access(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        if (!accessToken.startsWith(BEARER_PREFIX)) {
            throw new ExpandableException(ExceptionCode.INVALID_TOKEN);
        }

        authenticationService.access(accessToken.substring(BEARER_PREFIX.length()));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {
        if (!refreshToken.startsWith(BEARER_PREFIX)) {
            throw new ExpandableException(ExceptionCode.INVALID_TOKEN);
        }

        JwtTokenDto jwtTokenDto = authenticationService.refresh(refreshToken.substring(BEARER_PREFIX.length()));

        return ResponseEntity.status(HttpStatus.OK)
                .body(RefreshResponse.from(jwtTokenDto));
    }
}
