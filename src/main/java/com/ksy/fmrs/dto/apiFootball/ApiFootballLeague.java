package com.ksy.fmrs.dto.apiFootball;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiFootballLeague(
        String get,
//        Parameters parameters,
//        List<Object> errors,
        int results,
        Paging paging,
        List<ResponseItem> response
) {
    public record Parameters(
            String id
    ) {}

    public record Paging(
            int current,
            int total
    ) {}

    public record ResponseItem(
            League league,
            Country country,
            List<Season> seasons
    ) {}

    public record League(
            int id,
            String name,
            String type,
            String logo
    ) {}

    public record Country(
            String name,
            String code,
            String flag
    ) {}

    public record Season(
            int year,
            @JsonProperty("start")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate start,

            @JsonProperty("end")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate end,
            boolean current,
            Coverage coverage
    ) {}

    public record Coverage(
            Fixtures fixtures,
            boolean standings,
            boolean players,
            boolean top_scorers,
            boolean top_assists,
            boolean top_cards,
            boolean injuries,
            boolean predictions,
            boolean odds
    ) {}

    public record Fixtures(
            boolean events,
            boolean lineups,
            boolean statistics_fixtures,
            boolean statistics_players
    ) {}
}
