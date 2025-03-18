package com.study.expandable_application_auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    DUPLICATED_ID(HttpStatus.CONFLICT, "중복된 아이디입니다."),
    INVALID_ID(HttpStatus.UNAUTHORIZED, "유효하지 않는 아이디입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "유효하지 않는 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
