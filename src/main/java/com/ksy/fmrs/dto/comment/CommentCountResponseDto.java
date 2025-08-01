package com.ksy.fmrs.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCountResponseDto {
    private int count;

    public CommentCountResponseDto(int count) {
        this.count = count;
    }
}
