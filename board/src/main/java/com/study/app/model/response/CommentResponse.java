package com.study.app.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.study.app.model.dto.CommentDto;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentResponse {

    @JsonProperty("id")
    Long id;

    @JsonProperty("writer_id")
    Long writerId;

    @JsonProperty("board_id")
    Long boardId;

    @JsonProperty("root_comment_id")
    Long rootCommentId;

    @JsonProperty("content")
    String content;

    @JsonProperty("created_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    LocalDateTime updatedAt;

    public static CommentResponse from(CommentDto commentDto) {
        CommentDto rootCommentDto = commentDto.getRootComment();

        return new CommentResponse(
            commentDto.getId(),
            commentDto.getWriterId(),
            commentDto.getBoardId(),
            rootCommentDto != null ? rootCommentDto.getId() : null,
            commentDto.getContent(),
            commentDto.getCreatedAt(),
            commentDto.getUpdatedAt()
        );
    }
}
