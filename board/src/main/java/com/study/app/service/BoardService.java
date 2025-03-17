package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.BoardDto;
import com.study.app.model.entity.BoardEntity;
import com.study.app.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public void post(Long writerId, String title, String content) {
        BoardEntity boardEntity = BoardEntity.of(writerId, title, content);

        boardRepository.save(boardEntity);
    }

    public void update(Long requesterId, Long boardId, String title, String content) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_BOARD));

        if (!boardEntity.getWriterId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        boardEntity.update(title, content);

        boardRepository.save(boardEntity);
    }

    public void delete(Long requesterId, Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_BOARD));

        if (!boardEntity.getWriterId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        boardRepository.delete(boardEntity);
    }

    @Transactional(readOnly = true)
    public BoardDto detail(Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_BOARD));

        return BoardDto.from(boardEntity);
    }
}
