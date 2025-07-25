package com.ksy.fmrs.dto.comment;

import com.ksy.fmrs.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentResponseDto{
    private Long commentId;
    private Long playerId;
    private Long userId;
    private String username;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Long playerId, Long userId, String username, Comment comment) {
        this.playerId = playerId;
        this.userId = userId;
        this.username = username;
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.deleted = comment.isDeleted();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getModifiedDate();
    }
}
