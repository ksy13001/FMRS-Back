package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.exception.NullApiDataException;
import com.ksy.fmrs.util.NationNormalizer;
import com.ksy.fmrs.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApiFootballMapper {

    public List<Player> toEntity(ApiFootballPlayersStatistics dto) {

        List<Player> players = new ArrayList<>();
        dto.response().forEach(response -> {
            ApiFootballPlayersStatistics.PlayerDto player = response.player();
            players.add(Player.builder()
                    .name(player.name())
                    .birth(player.birth().date())
                    .firstName(player.firstname())
                    .lastName(player.lastname())
                    .playerApiId(player.id())
                    .height(StringUtils.extractNumber(player.height()))
                    .weight(StringUtils.extractNumber(player.weight()))
                    .imageUrl(player.photo())
                    .nationName(NationNormalizer.normalize(player.nationality()))
                    .mappingStatus(MappingStatus.UNMAPPED)
                    .build());
        });
        return players;
    }

    public List<Team> toEntity(ApiFootballTeamsByLeague dto) {
        if (dto == null || dto.response() == null || dto.response().isEmpty()) {
            return null;
        }

        List<Team> teams = new ArrayList<>();
        dto.response().forEach(response -> {
            teams.add(Team.builder()
                    .name(response.team().name())
                    .logoUrl(response.team().logo())
                    .teamApiId(response.team().id())
                    .build());
        });
        return teams;
    }

    public League toEntity(ApiFootballLeague dto) {
        if (dto == null || dto.response() == null || dto.response().isEmpty()) {
            return null;
        }

        ApiFootballLeague.League league = dto.response().getFirst().league();
        List<ApiFootballLeague.Season> seasons = dto.response().getFirst().seasons();

        return League.builder()
                .leagueApiId(league.id())
                .name(league.name())
                .logoUrl(league.logo())
                .currentSeason(seasons.getLast().year())
                .standing(seasons.getLast().coverage().standings())
                .startDate(seasons.getLast().start())
                .endDate(seasons.getLast().end())
                .leagueType(LeagueType.fromValue(league.type()))
                .build();
    }
}
