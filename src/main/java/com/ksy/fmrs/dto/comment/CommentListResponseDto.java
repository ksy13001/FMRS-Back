package com.ksy.fmrs.dto.comment;

import com.ksy.fmrs.dto.PaginationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentListResponseDto {

    private List<CommentResponseDto> comments;
    private PaginationDto pagination;

    public CommentListResponseDto(List<CommentResponseDto> comments, PaginationDto pagination) {
        this.comments = comments;
        this.pagination = pagination;
    }

}
