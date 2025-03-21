package com.ksy.fmrs.dto.apiFootball;

import java.util.List;

public record SquadApiResponseDto(
        String get,
        int results,
        Paging paging,
        List<ResponseItem> response
) {

    public static record Paging(
            int current,
            int total
    ) {}

    public static record ResponseItem(
            Team team,
            List<Player> players
    ) {}

    public static record Team(
            int id,
            String name,
            String logo
    ) {}

    public static record Player(
            int id,
            String name,
            int age,
            Integer number,  // null 값 가능
            String position,
            String photo
    ) {}
}
