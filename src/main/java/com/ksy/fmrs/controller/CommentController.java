package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.ApiResponse;
import com.ksy.fmrs.dto.comment.CommentListResponseDto;
import com.ksy.fmrs.dto.comment.CommentRequestDto;
import com.ksy.fmrs.dto.comment.CommentResponseDto;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/players/{playerId}/comments")
    public ResponseEntity<ApiResponse<CommentListResponseDto>> comment(
            @PathVariable Long playerId,
            @PageableDefault Pageable pageable) {
        return  ApiResponse.ok(
                commentService.getPlayerComments(playerId, pageable),
                "comment get success");
    }

    @PostMapping("/api/players/{playerId}/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> comment(
            @PathVariable Long playerId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.ok(
                commentService.save(userDetails.getId(), playerId, commentRequestDto.content()),
                "comment save success");
    }

    @PatchMapping("/api/players/{playerId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long commentId
    ){
        commentService.delete(commentId);
        return ApiResponse.ok(
                null,
                "comment delete success"
        );
    }

}
