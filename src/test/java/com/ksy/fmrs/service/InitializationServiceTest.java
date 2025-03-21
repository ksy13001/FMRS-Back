package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.player.GoalKeeperAttributes;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.StringUtils;
import io.micrometer.core.instrument.util.TimeUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InitializationServiceTest {

    @InjectMocks
    private InitializationService initializationService;
    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private FootballApiService footballApiService;
    @Mock
    private PlayerRepository playerRepository;


    @Test
    @DisplayName("리그, 팀 초기 생성 테스트")
    void 메서드명(){
        // given
        LeagueDetailsRequestDto leagueDetailsRequestDto = LeagueDetailsRequestDto.builder()
                .leagueApiId(1)
                .leagueName("League1")
                .leagueType(LeagueType.LEAGUE.getValue())
                .logoImageUrl("league1")
                .nationName("Nation1")
                .nationImageUrl("nation1")
                .currentSeason(2024)
                .Standing(true)
                .build();

        // when

        // then
    }


    @Test
    @DisplayName("선수 player_api_id 매핑 테스트")
    void mapping(){
        //given
        String fileName = "98031331-Manuel Akanji";
        String name = StringUtils.getPlayerNameFromFileName(fileName);
        String firstName = StringUtils.getFirstName(name).toUpperCase();
        String lastName = StringUtils.getLastName(name).toUpperCase();
        LocalDate birthDate = LocalDate.of(1995,7,19);
        String nation = "SWITZERLAND";

        String dbFirstName = "MANUEL";
        String dbLastName = "AKANJI";
        String dbNation = "SWITZERLAND";
        LocalDate dbBirthDate = LocalDate.of(1995,7,19);

        FmPlayerDto.GoalKeeperAttributesDto goalKeeperAttributes = new FmPlayerDto.GoalKeeperAttributesDto();
        goalKeeperAttributes.setAerialAbility(10);
        Player akanji = Player.builder()
                .firstName(dbFirstName)
                .lastName(dbLastName)
                .birth(birthDate)
                .nationName(dbNation)
                .birth(dbBirthDate)
                .build();

        List<Player> findPlayer = new ArrayList<>();
        findPlayer.add(akanji);

        FmPlayerDto.NationDto nationDto = new FmPlayerDto.NationDto();
        nationDto.setName(nation);

        FmPlayerDto fmPlayerDto = new  FmPlayerDto();
        fmPlayerDto.setGoalKeeperAttributes(goalKeeperAttributes);
        fmPlayerDto.setName(fileName);
        fmPlayerDto.setBorn(birthDate);
        fmPlayerDto.setNation(nationDto);

        List<FmPlayerDto> fmPlayers = new ArrayList<>();
        fmPlayers.add(fmPlayerDto);
        //when
        when(playerRepository.searchPlayerByFm(firstName, lastName, birthDate, nation))
                .thenReturn(findPlayer);
        initializationService.updatePlayerFmStat(fmPlayers);
        //then
        Assertions.assertThat(akanji.getFirstName()).isEqualTo(dbFirstName);
        Assertions.assertThat(akanji.getGoalKeeperAttributes().getAerialAbility()).isEqualTo(10);
    }
}