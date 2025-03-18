package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CommentUpdateRequest {

    @JsonProperty(value = "content", required = true)
    String content;
}
