package com.study.app.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.app.model.dto.BoardDto;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class BoardResponse {

    @JsonProperty("id")
    Long id;

    @JsonProperty("title")
    String title;

    @JsonProperty("content")
    String content;

    @JsonProperty("writerId")
    Long author;

    @JsonProperty("created_at")
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    LocalDateTime updatedAt;

    @JsonProperty("comments")
    List<CommentResponse> comments;

    public static BoardResponse from(BoardDto boardDto) {
        List<CommentResponse> comments = boardDto.getComments().stream()
                .map(CommentResponse::from)
                .toList();

        return new BoardResponse(
                boardDto.getId(),
                boardDto.getTitle(),
                boardDto.getContent(),
                boardDto.getWriterId(),
                boardDto.getCreatedAt(),
                boardDto.getUpdatedAt(),
                comments
        );
    }
}
