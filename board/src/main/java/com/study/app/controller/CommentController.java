package com.study.app.controller;

import com.study.app.model.request.CommentRequest;
import com.study.app.model.request.CommentUpdateRequest;
import com.study.app.model.response.CommentResponse;
import com.study.app.service.AuthenticationService;
import com.study.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final CommentService commentService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Void> comment(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody CommentRequest request
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));

        commentService.comment(authenticatedId, request.getBoardId(), request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{comment_id}")
    public ResponseEntity<Void> update(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody CommentUpdateRequest request,
            @PathVariable("comment_id") Long commentId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        commentService.update(authenticatedId, commentId, request.getContent());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable("comment_id") Long commentId
    ) {
        Long authenticatedId = authenticationService.authenticate(extractToken(authorizationHeader));
        commentService.delete(authenticatedId, commentId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{board_id}")
    public ResponseEntity<List<CommentResponse>> detail(
            @PathVariable("board_id") Long boardId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<CommentResponse> responseBody = commentService.getCommentByBoard(boardId, pageable)
                .map(CommentResponse::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    private static String extractToken(String authorizationHeader) {
        return authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
    }
}
