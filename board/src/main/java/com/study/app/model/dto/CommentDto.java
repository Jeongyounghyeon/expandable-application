package com.study.app.model.dto;

import com.study.app.model.entity.CommentEntity;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Value
@Log4j2
public class CommentDto {

    Long id;
    Long writerId;
    Long boardId;
    CommentDto rootComment;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static CommentDto from(CommentEntity commentEntity) {
        CommentEntity rootCommentEntity = commentEntity.getRootComment();

        log.info("rootCommentEntity: {}", rootCommentEntity);

        return new CommentDto(
                commentEntity.getId(),
                commentEntity.getWriterId(),
                commentEntity.getBoard().getId(),
                rootCommentEntity != null ? CommentDto.from(rootCommentEntity) : null,
                commentEntity.getContent(),
                commentEntity.getCreatedAt(),
                commentEntity.getUpdatedAt()
        );
    }
}
