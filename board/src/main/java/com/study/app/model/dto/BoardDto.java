package com.study.app.model.dto;

import com.study.app.model.entity.BoardEntity;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class BoardDto {

    Long id;
    String title;
    String content;
    Long writerId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<CommentDto> comments;

    public static BoardDto from(BoardEntity boardEntity) {
        List<CommentDto> comments = boardEntity.getComments().stream()
                .map(CommentDto::from)
                .toList();

        return new BoardDto(
                boardEntity.getId(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                boardEntity.getWriterId(),
                boardEntity.getCreatedAt(),
                boardEntity.getUpdatedAt(),
                comments
        );
    }
}
