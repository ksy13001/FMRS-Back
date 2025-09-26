package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.exception.NullApiDataException;
import com.ksy.fmrs.exception.SkippableSyncException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ksy.fmrs.service.ApiFootballValidator.*;

class ApiFootballValidatorTest {


    private ApiFootballValidator validator;

    private final List<String> errors = List.of("some_error_messages");

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
    @DisplayName("response is null && error is not empty, 예외 발생")
    void validate_Team_Empty_Response(){
        testTeamValidator(
                new ApiFootballTeamsByLeague(
                        "team", null, List.of("some_error_messages"), 0, null, null
                ),
                RESPONSE_IS_Empty
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
    @DisplayName("response is null && error is not null, 예외 발생")
    void validate_League_Empty_Response(){
        testLeagueValidator(
                new ApiFootballLeague(
                        "League",null, List.of("some_error_messages"), 0, null, null
                ),
                RESPONSE_IS_Empty
        );
    }

    @Test
    @DisplayName("response is empty AND error is empty, SkippableException 발생")
    void validate_Squad_Empty_Response(){
        // given
        ApiFootballSquad squad = new ApiFootballSquad(
                "1", null, List.of(), 1, null, List.of()
        );
        // when && then
        Assertions.assertThatThrownBy(() -> validator.validateSquad(squad))
                .isInstanceOf(SkippableSyncException.class);
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