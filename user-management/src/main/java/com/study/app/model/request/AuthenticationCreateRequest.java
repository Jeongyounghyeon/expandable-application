package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value(staticConstructor = "of")
public class AuthenticationCreateRequest {

    @JsonProperty("id")
    String id;

    @JsonProperty("password")
    String password;

    @JsonProperty("user_id")
    Long userId;
}
