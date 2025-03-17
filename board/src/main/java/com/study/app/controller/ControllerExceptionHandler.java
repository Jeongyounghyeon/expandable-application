package com.study.app.controller;

import com.study.app.exception.ExpandableException;
import com.study.app.model.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ExpandableException.class)
    public ResponseEntity<ExceptionResponse> handleExpandableException(ExpandableException e) {
        return ResponseEntity
                .status(e.getCode().getHttpStatus())
                .body(ExceptionResponse.from(e));
    }
}
