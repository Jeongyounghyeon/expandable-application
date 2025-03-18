package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.BoardDto;
import com.study.app.model.entity.BoardEntity;
import com.study.app.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = BoardService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BoardServiceTest {

    private final BoardService boardService;

    @MockitoBean
    private final BoardRepository boardRepository;

    @Test
    public void post() {
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        boardService.post(writerId, title, content);

        verify(boardRepository).save(any());
    }

    @Test
    public void update() {
        Long requesterId = 1L;
        Long boardId = 1L;
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(BoardEntity.of(writerId, title, content)));

        String updatedTitle = "updated_title";
        String updatedContent = "updated_content";
        boardService.update(requesterId, boardId, updatedTitle, updatedContent);

        verify(boardRepository).save(any());
    }

    @Test
    public void update_실패_테스트_없는_게시판() {
        Long requesterId = 1L;
        Long boardId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> boardService.update(requesterId, boardId, title, content));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }

    @Test
    public void update_실패_테스트_게시글_작성자와_다른_유저() {
        Long requesterId = 2L;
        Long boardId = 1L;
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(BoardEntity.of(writerId, title, content)));

        ExpandableException e = assertThrows(ExpandableException.class, () -> boardService.update(requesterId, boardId, title, content));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    public void delete() {
        Long requesterId = 1L;
        Long boardId = 1L;
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(BoardEntity.of(writerId, title, content)));

        boardService.delete(requesterId, boardId);

        verify(boardRepository).delete(any());
    }

    @Test
    public void delete_실패_테스트_없는_게시판() {
        Long requesterId = 1L;
        Long boardId = 1L;

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> boardService.delete(requesterId, boardId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }

    @Test
    public void delete_실패_게시글_작성자와_다른_유저() {
        Long requesterId = 2L;
        Long boardId = 1L;
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(BoardEntity.of(writerId, title, content)));

        ExpandableException e = assertThrows(ExpandableException.class, () -> boardService.delete(requesterId, boardId));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    public void detail() {
        Long boardId = 1L;
        Long writerId = 1L;
        String title = "title";
        String content = "content";

        given(boardRepository.findById(boardId)).willReturn(Optional.of(BoardEntity.of(writerId, title, content)));

        BoardDto boardDto = boardService.detail(boardId);

        assertEquals(boardDto.getWriterId(), writerId);
        assertEquals(boardDto.getTitle(), title);
        assertEquals(boardDto.getContent(), content);
    }

    @Test
    public void detail_실패_테스트_없는_게시판() {
        Long boardId = 1L;

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> boardService.detail(boardId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_BOARD);
    }
}
