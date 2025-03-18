package com.study.expandable_application_auth.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AuthenticationCreateRequest {

    @JsonProperty("id")
    String id;

    @JsonProperty("password")
    String password;

    @JsonProperty("user_id")
    Long userId;
}
