package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.Comment;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationDto {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;

    public PaginationDto(Page<Comment> page) {
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}
