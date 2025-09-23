package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import com.ksy.fmrs.exception.NullApiDataException;
import org.springframework.stereotype.Component;

@Component
public class ApiFootballValidator{

    public static final String DTO_IS_NULL = "validate: ApiFootballPlayersStatistics is null";
    public static final String RESPONSE_IS_NULL = "validate: response is null";
    public static final String PLAYER_IS_NULL = "validate: player is null";
    public static final String STATISTICS_IS_NULL = "validate: player statistics is null";
    public static final String LEAGUE_IS_NULL = "validate: league is null";
    public static final String SEASONS_IS_NULL = "validate: seasons is null";
    public static final String TEAM_IS_NULL = "validate: team is null";

    public void validateLeague(ApiFootballLeague dto){
        if(dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }
        if (dto.errors() == null){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_NULL);
        }
        if (dto.response().getFirst().league() == null){
            throw new NullApiDataException(LEAGUE_IS_NULL);
        }
        if (dto.response().getFirst().seasons() == null || dto.response().getFirst().seasons().isEmpty()){
            throw new NullApiDataException(SEASONS_IS_NULL);
        }
    }

    public void validateTeam(ApiFootballTeamsByLeague dto){
        if(dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }
        if (dto.errors() == null){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_NULL);
        }
        for(ApiFootballTeamsByLeague.Response response : dto.response()){
            if (response.team() == null){
                throw new NullApiDataException(TEAM_IS_NULL);
            }
        }
    }

    public void validatePlayerStatistics(ApiFootballPlayersStatistics dto){
        if(dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }
        if (dto.errors() == null){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_NULL);
        }
        for (ApiFootballPlayersStatistics.PlayerWrapperDto playerWrapperDto : dto.response()){
            if (playerWrapperDto.player() == null){
                throw new NullApiDataException(PLAYER_IS_NULL);
            }

            if (playerWrapperDto.statistics() == null){
                throw new NullApiDataException(STATISTICS_IS_NULL);
            }
        }
    }

    public void validateSquad(ApiFootballSquad dto){
        if (dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }
        if (dto.errors() == null){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_NULL);
        }
    }
}
