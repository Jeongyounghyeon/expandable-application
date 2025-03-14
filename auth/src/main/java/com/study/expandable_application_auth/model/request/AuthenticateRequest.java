package com.study.expandable_application_auth.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AuthenticateRequest {

    @JsonProperty("id")
    String id;

    @JsonProperty("password")
    String password;
}
