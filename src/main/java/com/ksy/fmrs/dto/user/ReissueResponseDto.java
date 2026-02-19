package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReissueResponseDto {
    private boolean reissued;

    public static ReissueResponseDto success() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.reissued = true;
        return dto;
    }
}
