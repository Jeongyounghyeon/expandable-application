package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserUpdateRequest {

    @JsonProperty(value = "username", required = true)
    String username;
}
