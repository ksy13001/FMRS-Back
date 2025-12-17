package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ksy.fmrs.dto.apiFootball.*;
import com.ksy.fmrs.exception.ErrorResponseException;
import com.ksy.fmrs.exception.NullApiDataException;
import com.ksy.fmrs.exception.EmptyResponseException;
import com.ksy.fmrs.exception.SkippableSyncException;
import org.springframework.stereotype.Component;

@Component
public class ApiFootballValidator{

    public static final String DTO_IS_NULL = "validate: ApiFootballPlayersStatistics is null";
    public static final String RESPONSE_IS_Empty = "validate: response is null";
    public static final String PLAYER_IS_NULL = "validate: player is null";
    public static final String STATISTICS_IS_NULL = "validate: player statistics is null";
    public static final String LEAGUE_IS_NULL = "validate: league is null";
    public static final String SEASONS_IS_NULL = "validate: seasons is null";
    public static final String TEAM_IS_NULL = "validate: team is null";
    public static final String ERROR_RESPONSE="validate: error response";
    public static final String OVER_PAGE = "validate: Paging error: start page should be 1";

    public void validateLeague(ApiFootballLeague dto){
        if(dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }
        if (dto.errors().isEmpty()){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_Empty);
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
        if (dto.errors().isEmpty()){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_Empty);
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
        if (dto.errors().isEmpty()){
            return;
        }
        if (dto.response() == null || dto.response().isEmpty()){
            throw new NullApiDataException(RESPONSE_IS_Empty);
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

    public void validateSquad(ApiFootballSquad dto) {
        if (dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }

        if (!dto.errors().isEmpty()){
            throw new ErrorResponseException(ERROR_RESPONSE);
        }

        if(dto.response().isEmpty()){
           throw new EmptyResponseException(RESPONSE_IS_Empty);
        }

    }

    public void validateTransfer(ApiFootballTransfers dto) {
        if (dto == null){
            throw new NullApiDataException(DTO_IS_NULL);
        }

        JsonNode errors = dto.errors();
        if (errors != null && !errors.isEmpty()){
            throw new ErrorResponseException(ERROR_RESPONSE);
        }

        if(dto.response().isEmpty()){
            throw new EmptyResponseException(RESPONSE_IS_Empty);
        }

        if(dto.paging().total() != 1){
            throw new IllegalArgumentException(OVER_PAGE);
        }
    }

}
