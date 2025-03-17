package com.study.app.controller;

import com.study.app.model.request.BoardPostRequest;
import com.study.app.model.request.BoardUpdateRequest;
import com.study.app.model.response.BoardResponse;
import com.study.app.service.AuthenticationService;
import com.study.app.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final BoardService boardService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Void> post(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody BoardPostRequest request
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        boardService.post(authenticatedId, request.getTitle(), request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{board_id}")
    public ResponseEntity<Void> update(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody BoardUpdateRequest request,
            @PathVariable("board_id") Long boardId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        boardService.update(authenticatedId, boardId, request.getTitle(), request.getContent());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{board_id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable("board_id") Long boardId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        boardService.delete(authenticatedId, boardId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{board_id}")
    public ResponseEntity<BoardResponse> detail(@PathVariable("board_id") Long boardId) {
        BoardResponse responseBody = BoardResponse.from(boardService.detail(boardId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    private static String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
    }
}
