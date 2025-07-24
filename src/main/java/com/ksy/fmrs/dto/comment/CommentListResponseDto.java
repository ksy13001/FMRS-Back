package com.ksy.fmrs.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentListResponseDto {

    private List<CommentResponseDto> comments;
    private int totalComments;

    public CommentListResponseDto(List<CommentResponseDto> comments) {
        this.comments = comments;
        this.totalComments = comments.size();
    }

}
