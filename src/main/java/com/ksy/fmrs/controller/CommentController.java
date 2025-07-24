package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.comment.CommentListResponseDto;
import com.ksy.fmrs.dto.comment.CommentRequestDto;
import com.ksy.fmrs.dto.comment.CommentResponseDto;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class CommentController {
    private CommentService commentService;

    @GetMapping("/api/comment/{playerId}/comments")
    public ResponseEntity<CommentListResponseDto> comment(@PathVariable Long playerId) {
        return  ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getPlayerComments(playerId));
    }

    @PostMapping("/api/players/{playerId}/comments")
    public ResponseEntity<CommentResponseDto> comment(
            @PathVariable Long playerId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.save(userDetails.getId(), playerId, commentRequestDto.content()));
    }

}
