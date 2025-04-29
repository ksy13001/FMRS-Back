package com.ksy.fmrs.dto.apiFootball;

import java.util.List;

// ── 최상위 응답 객체
public record PlayerStatisticApiDto(
        String get,
//        Parameters parameters,
//        List<String> errors,
        int results,
        Paging paging,
        List<PlayerResponse> response
) {

    // ── 요청 파라미터
    public record Parameters(
            String id,
            String team,
            String season
    ) {
    }

    // ── 페이징 정보
    public record Paging(
            int current,
            int total
    ) {
    }

    // ── response 배열 요소
    public record PlayerResponse(
            Player player,
            List<Statistic> statistics
    ) {
    }

    // ── 선수 기본 정보
    public record Player(
            int id,
            String name,
            String firstname,
            String lastname,
            int age,
            Birth birth,
            String nationality,
            String height,
            String weight,
            boolean injured,
            String photo
    ) {
    }

    // ── 출생 정보
    public record Birth(
            String date,
            String place,
            String country
    ) {
    }

    // ── 통계 정보
    public record Statistic(
            Team team,
            League league,
            Games games,
            Substitutes substitutes,
            Shots shots,
            Goals goals,
            Passes passes,
            Tackles tackles,
            Duels duels,
            Dribbles dribbles,
            Fouls fouls,
            Cards cards,
            Penalty penalty
    ) {
    }

    // ── 팀 정보
    public record Team(
            int id,
            String name,
            String logo
    ) {
    }

    // ── 리그 정보
    public record League(
            int id,
            String name,
            String country,
            String logo,
            String flag,
            int season
    ) {
    }

    // ── 경기 정보
    public record Games(
            int appearences,
            int lineups,
            int minutes,
            Integer number,
            String position,
            String rating,
            boolean captain
    ) {
    }

    // ── 교체 정보
    public record Substitutes(
            int in,
            int out,
            int bench
    ) {
    }

    // ── 슛 정보
    public record Shots(
            Integer total,
            Integer on
    ) {
    }

    // ── 골 정보
    public record Goals(
            Integer total,
            Integer conceded,
            Integer assists,
            Integer saves
    ) {
    }

    // ── 패스 정보
    public record Passes(
            Integer total,
            Integer key,
            Double accuracy
    ) {
    }

    // ── 태클 정보
    public record Tackles(
            Integer total,
            Integer blocks,
            Integer interceptions
    ) {
    }

    // ── 대결(duel) 정보
    public record Duels(
            Integer total,
            Integer won
    ) {
    }

    // ── 드리블 정보
    public record Dribbles(
            Integer attempts,
            Integer success,
            Integer past
    ) {
    }

    // ── 파울 정보
    public record Fouls(
            Integer drawn,
            Integer committed
    ) {
    }

    // ── 카드 정보
    public record Cards(
            Integer yellow,
            Integer yellowred,
            Integer red
    ) {
    }

    // ── 페널티 정보
    public record Penalty(
            Integer won,
            Integer commited,
            Integer scored,
            Integer missed,
            Integer saved
    ) {
    }
}