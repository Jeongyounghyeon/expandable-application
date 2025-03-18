package com.study.app.exception;

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
