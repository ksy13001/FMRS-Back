package com.ksy.fmrs.dto.apiFootball;

import java.util.List;

public record TeamListApiResponseDto(
        String get,
        Parameters parameters,
        List<String> errors,
        int results,
        Paging paging,
        List<Response> response
) {
    public record Parameters(
            String id,
            String league
    ) {}

    public record Paging(
            int current,
            int total
    ) {}

    public record Response(
            Team team,
            Venue venue
    ) {}

    public record Team(
            int id,
            String name,
            String code,
            String country,
            int founded,
            boolean national,
            String logo
    ) {}

    public record Venue(
            int id,
            String name,
            String address,
            String city,
            int capacity,
            String surface,
            String image
    ) {}
}
