package com.ksy.fmrs.dto.apiFootball;

import java.util.List;
import java.util.Map;

public record PlayerStatisticsApiResponseDto(
        String get,
//        ParametersDto parameters,
//        List<Map<String, Object>> errors,
        int results,
        PagingDto paging,
        List<PlayerWrapperDto> response
) {

    public record ParametersDto(
            String id,
            String season
    ) {}

    public record PagingDto(
            int current,
            int total
    ) {}

    public record PlayerWrapperDto(
            PlayerDto player,
            List<StatisticDto> statistics
    ) {}

    public record PlayerDto(
            int id,
            String name,
            String firstname,
            String lastname,
            int age,
            BirthDto birth,
            String nationality,
            String height,
            String weight,
            boolean injured,
            String photo
    ) {}

    public record BirthDto(
            String date,
            String place,
            String country
    ) {}

    public record StatisticDto(
            TeamDto team,
            LeagueDto league,
            GamesDto games,
            SubstitutesDto substitutes,
            ShotsDto shots,
            GoalsDto goals,
            PassesDto passes,
            TacklesDto tackles,
            DuelsDto duels,
            DribblesDto dribbles,
            FoulsDto fouls,
            CardsDto cards,
            PenaltyDto penalty
    ) {

        public record TeamDto(
                int id,
                String name,
                String logo
        ) {}

        public record LeagueDto(
                int id,
                String name,
                String country,
                String logo,
                String flag,
                String season
        ) {}

        public record GamesDto(
                Integer appearences,
                Integer lineups,
                Integer minutes,
                Integer number,
                String position,
                String rating,
                boolean captain
        ) {}

        public record SubstitutesDto(
                Integer in,
                Integer out,
                Integer bench
        ) {}

        public record ShotsDto(
                Integer total,
                Integer on
        ) {}

        public record GoalsDto(
                Integer total,
                Integer conceded,
                Integer assists,
                Integer saves
        ) {}

        public record PassesDto(
                Integer total,
                Integer key,
                Integer accuracy
        ) {}

        public record TacklesDto(
                Integer total,
                Integer blocks,
                Integer interceptions
        ) {}

        public record DuelsDto(
                Integer total,
                Integer won
        ) {}

        public record DribblesDto(
                Integer attempts,
                Integer success,
                Integer past
        ) {}

        public record FoulsDto(
                Integer drawn,
                Integer committed
        ) {}

        public record CardsDto(
                Integer yellow,
                Integer yellowred,
                Integer red
        ) {}

        public record PenaltyDto(
                Integer won,
                Integer commited,
                Integer scored,
                Integer missed,
                Integer saved
        ) {}
    }
}
