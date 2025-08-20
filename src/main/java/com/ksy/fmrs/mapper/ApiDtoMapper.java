package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ApiDtoMapper {

    public List<Team> toEntity(ApiFootballTeamsByLeague apiFootballTeamsByLeague) {
        if (apiFootballTeamsByLeague == null || apiFootballTeamsByLeague.response() == null || apiFootballTeamsByLeague.response().isEmpty()) {
            return null;
        }

        List<Team> teams = new ArrayList<>();
        apiFootballTeamsByLeague.response().forEach(response -> {
            teams.add(Team.builder()
                    .name(response.team().name())
                    .logoUrl(response.team().logo())
                    .teamApiId(response.team().id())
                    .build());
        });
        return teams;
    }

    public League toEntity(ApiFootballLeague apiFootballLeague){
        if (apiFootballLeague == null || apiFootballLeague.response() == null || apiFootballLeague.response().isEmpty()) {
            return null;
        }

        ApiFootballLeague.League league = apiFootballLeague.response().getFirst().league();
        List<ApiFootballLeague.Season> seasons = apiFootballLeague.response().getFirst().seasons();

        return League.builder()
                .leagueApiId(league.id())
                .name(league.name())
                .logoUrl(league.logo())
                .currentSeason(seasons.getLast().year())
                .standing(seasons.getLast().coverage().standings())
                .startDate(seasons.getLast().start())
                .endDate(seasons.getLast().end())
                .leagueType((Objects.equals(league.type(),
                        LeagueType.LEAGUE.getValue())) ? LeagueType.LEAGUE : LeagueType.CUP)
                .build();
    }
}
