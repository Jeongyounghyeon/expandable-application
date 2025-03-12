package com.study.expandable_application_auth.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.expandable_application_auth.exception.ExpandableException;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class ExceptionResponse {

    @JsonProperty("message")
    String message;

    public static ExceptionResponse from(ExpandableException e) {
        return ExceptionResponse.of(e.getCode().getMessage());
    }
}
