package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class BoardPostRequest {

    @JsonProperty(value = "title", required = true)
    String title;

    @JsonProperty(value = "content", required = true)
    String content;
}
