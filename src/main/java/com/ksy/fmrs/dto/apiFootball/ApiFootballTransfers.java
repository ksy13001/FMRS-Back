package com.ksy.fmrs.dto.apiFootball;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record ApiFootballTransfers(
        @JsonProperty("get")
        String requestType,
        JsonNode parameters,
        JsonNode errors,
        int results,
        PagingDto paging,
        List<PlayerTransfersDto> response
) {


    public record PagingDto(
            int current,
            int total
    ) {
    }

    public record PlayerTransfersDto(
            PlayerSummaryDto player,
            OffsetDateTime update,
            List<TransferDto> transfers
    ) {
    }

    public record PlayerSummaryDto(
            Integer id,
            String name
    ) {
    }

    public record TransferDto(
            LocalDate date,
            String type,
            TransferTeamsDto teams
    ) {
    }

    public record TransferTeamsDto(
            @JsonProperty("in")
            TransferTeamDto incomingTeam,
            @JsonProperty("out")
            TransferTeamDto outgoingTeam
    ) {
    }

    public record TransferTeamDto(
            Integer id,
            String name,
            String logo
    ) {
    }
}
