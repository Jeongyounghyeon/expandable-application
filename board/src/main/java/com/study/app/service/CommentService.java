package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.CommentDto;
import com.study.app.model.entity.BoardEntity;
import com.study.app.model.entity.CommentEntity;
import com.study.app.repository.BoardRepository;
import com.study.app.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public void comment(Long writerId, Long boardId, String content) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_BOARD));

        CommentEntity commentEntity = CommentEntity.builder(writerId, boardEntity, content)
                .build();

        commentRepository.save(commentEntity);
    }

    public void comment(Long writerId, Long boardId, String content, Long rootCommentId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_BOARD));

        CommentEntity rootCommentEntity = commentRepository.findById(rootCommentId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_COMMENT));

        CommentEntity commentEntity = CommentEntity.builder(writerId, boardEntity, content)
                .rootComment(rootCommentEntity)
                .build();

        commentRepository.save(commentEntity);
    }

    public void update(Long requesterId, Long commentId, String content) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_COMMENT));

        if (!commentEntity.getWriterId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        commentEntity.update(content);

        commentRepository.save(commentEntity);
    }

    public void delete(Long requesterId, Long boardId) {
        CommentEntity commentEntity = commentRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_COMMENT));

        if (!commentEntity.getWriterId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        commentRepository.delete(commentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentByBoard(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) {
            throw new ExpandableException(ExceptionCode.NOT_FOUND_BOARD);
        }

        Page<CommentEntity> commentEntities = commentRepository.findAllByBoardId(boardId, pageable);

        return commentEntities.map(CommentDto::from);
    }
}
