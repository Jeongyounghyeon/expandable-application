package com.study.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CommentRequest {

    @JsonProperty(value = "board_id", required = true)
    Long boardId;

    @JsonProperty(value = "content", required = true)
    String content;

    @JsonProperty("root_comment_id")
    Long rootCommentId;
}
