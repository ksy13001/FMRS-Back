package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.ValidateResponse;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.exception.NullApiDataException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ksy.fmrs.service.ApiFootballValidator.*;

class ApiFootballValidatorTest {


    private ApiFootballValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ApiFootballValidator();
    }

    @Test
    void validate_Player_Empty_Dto() {
        testPlayerValidator(
                null,
                DTO_IS_NULL);
    }

    @Test
    void validate_Player_Empty_Response() {
        testPlayerValidator(
                new ApiFootballPlayersStatistics(
                        "players", null, null, 0, null, List.of()
                ),
                RESPONSE_IS_NULL);
    }

    @Test
    void validate_Player_Empty_Player(){
        testPlayerValidator(
                new ApiFootballPlayersStatistics(
                        "players", null, null, 1,
                        null, List.of(new ApiFootballPlayersStatistics.PlayerWrapperDto(null, null))
                )
                , PLAYER_IS_NULL
        );
    }


    @Test
    void validate_Team_Empty_Dto(){
        testTeamValidator(
                null,
                DTO_IS_NULL
        );
    }

    @Test
    void validate_Team_Empty_Response(){
        testTeamValidator(
                new ApiFootballTeamsByLeague(
                        "team", null, null, 0, null, null
                ),
                RESPONSE_IS_NULL
        );
    }

    @Test
    void validate_League_Empty_Dto(){
        testLeagueValidator(
                null,
                DTO_IS_NULL
        );
    }

    @Test
    void validate_League_Empty_Response(){
        testLeagueValidator(
                new ApiFootballLeague(
                        "League", 0, null, null
                ),
                RESPONSE_IS_NULL
        );
    }

    private void testTeamValidator(ApiFootballTeamsByLeague dto, String message) {
        Assertions.assertThatThrownBy(()->validator.validateTeam(dto))
                .isInstanceOf(NullApiDataException.class)
                .hasMessage(message);
    }

    private void testLeagueValidator(ApiFootballLeague dto, String message) {
        Assertions.assertThatThrownBy(()->validator.validateLeague(dto))
                .isInstanceOf(NullApiDataException.class)
                .hasMessage(message);
    }

    private void testPlayerValidator(ApiFootballPlayersStatistics dto, String message) {
        Assertions.assertThatThrownBy(()->validator.validatePlayerStatistics(dto))
                .isInstanceOf(NullApiDataException.class)
                .hasMessage(message);
    }
}