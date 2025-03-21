package com.study.expandable_application_auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpandableException extends RuntimeException {

    private final ExceptionCode code;

    public ExpandableException(ExceptionCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
