package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserCreateRequest {

    @JsonProperty(value = "username", required = true)
    String username;

    @JsonProperty(value = "id", required = true)
    String id;

    @JsonProperty(value = "password", required = true)
    String password;
}
