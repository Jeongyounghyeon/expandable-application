package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.BoardDto;
import com.study.app.model.dto.CommentDto;
import com.study.app.model.entity.BoardEntity;
import com.study.app.model.entity.CommentEntity;
import com.study.app.repository.BoardRepository;
import com.study.app.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = CommentService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class CommentServiceTest {

    private final CommentService commentService;

    @MockitoBean
    private final CommentRepository commentRepository;

    @MockitoBean
    private final BoardRepository boardRepository;

    @Test
    void comment() {
        Long writerId = 1L;
        Long boardId = 1L;
        String commentContent = "This is content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(mock(BoardEntity.class)));

        commentService.comment(writerId, boardId, commentContent);

        verify(commentRepository).save(any());
    }

    @Test
    void comment_실패_테스트_없는_게시판() {
        Long writerId = 1L;
        Long boardId = 1L;
        String commentContent = "This is content";

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.comment(writerId, boardId, commentContent));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }

    @Test
    void commentWithRootComment() {
        Long writerId = 1L;
        Long boardId = 1L;
        String commentContent = "This is content";
        Long rootCommentId = 2L;

        given(boardRepository.findById(boardId)).willReturn(Optional.of(mock(BoardEntity.class)));
        given(commentRepository.findById(rootCommentId)).willReturn(Optional.of(mock(CommentEntity.class)));

        commentService.comment(writerId, boardId, commentContent, rootCommentId);

        verify(commentRepository).save(any());
    }

    @Test
    void commentWithRootComment_실패_테스트_없는_게시판() {
        Long writerId = 1L;
        Long boardId = 1L;
        String commentContent = "This is content";
        Long rootCommentId = 2L;

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.comment(writerId, boardId, commentContent, rootCommentId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }

    @Test
    void commentWithRootComment_실패_테스트_없는_부모_댓글() {
        Long writerId = 1L;
        Long boardId = 1L;
        String commentContent = "This is content";
        Long rootCommentId = 2L;

        given(boardRepository.findById(boardId)).willReturn(Optional.of(mock(BoardEntity.class)));
        given(commentRepository.findById(rootCommentId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.comment(writerId, boardId, commentContent, rootCommentId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_COMMENT);
    }

    @Test
    void update() {
        Long requesterId = 1L;
        Long commentId = 1L;
        Long writerId = 1L;
        String content = "content";

        CommentEntity commentEntity = CommentEntity
                .builder(writerId, mock(BoardEntity.class), content)
                .build();
        given(commentRepository.findById(commentId)).willReturn(Optional.of(commentEntity));

        String updatedContent = "updated_content";
        commentService.update(requesterId, commentId, updatedContent);

        verify(commentRepository).save(any());
    }

    @Test
    void update_실패_테스트_없는_댓글() {
        Long requesterId = 1L;
        Long commentId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        String updatedContent = "updated_content";
        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.update(requesterId, commentId, updatedContent));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_COMMENT);
    }

    @Test
    void update_실패_테스트_댓글_작성자와_다른_유저() {
        Long requesterId = 2L;
        Long commentId = 1L;
        Long writerId = 1L;
        String content = "content";

        CommentEntity commentEntity = CommentEntity
                .builder(writerId, mock(BoardEntity.class), content)
                .build();
        given(commentRepository.findById(commentId)).willReturn(Optional.of(commentEntity));

        String updatedContent = "updated_content";
        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.update(requesterId, commentId, updatedContent));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    void delete() {
        Long requesterId = 1L;
        Long commentId = 1L;
        Long writerId = 1L;
        String content = "content";

        CommentEntity commentEntity = CommentEntity
                .builder(writerId, mock(BoardEntity.class), content)
                .build();
        given(commentRepository.findById(commentId)).willReturn(Optional.of(commentEntity));

        commentService.delete(requesterId, commentId);

        verify(commentRepository).delete(any());
    }

    @Test
    void delete_실패_테스트없는_댓글() {
        Long requesterId = 1L;
        Long commentId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.delete(requesterId, commentId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_COMMENT);
    }

    @Test
    void delete_실패_테스트_댓글_작성자와_다른_유저() {
        Long requesterId = 2L;
        Long commentId = 1L;
        Long writerId = 1L;
        String content = "content";

        CommentEntity commentEntity = CommentEntity
                .builder(writerId, mock(BoardEntity.class), content)
                .build();
        given(commentRepository.findById(commentId)).willReturn(Optional.of(commentEntity));

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.delete(requesterId, commentId));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    void getCommentByBoard() {
        Long boardId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        given(boardRepository.existsById(boardId)).willReturn(true);
        given(commentRepository.findAllByBoardId(boardId, pageable)).willReturn(Page.empty());

        commentService.getCommentByBoard(boardId, pageable);

        verify(commentRepository).findAllByBoardId(boardId, pageable);
    }

    @Test
    void getCommentByBoard_실패_테스트_없는_게시판() {
        Long boardId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        given(commentRepository.existsById(boardId)).willReturn(false);

        ExpandableException e = assertThrows(ExpandableException.class, () -> commentService.getCommentByBoard(boardId, pageable));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }
}
