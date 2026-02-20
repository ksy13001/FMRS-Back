package com.ksy.fmrs.dto.transfer;

import com.ksy.fmrs.domain.enums.TransferType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransferRequestDto(
        Integer playerApiId,
        Integer fromTeamApiId,
        Integer toTeamApiId,
        TransferType transferType,
        Double fee,
        String currency,
        LocalDate date,
        LocalDateTime update
) {
}
