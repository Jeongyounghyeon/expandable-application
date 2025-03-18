package com.study.app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 이름입니다."),
    INVALID_ACCESS_USER(HttpStatus.UNAUTHORIZED, "권한이 없는 접근자입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    AUTHENTICATION_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 정보 생성에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
