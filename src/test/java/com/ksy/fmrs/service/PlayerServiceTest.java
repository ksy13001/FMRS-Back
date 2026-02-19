package com.ksy.fmrs.service;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.nation.NationDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.DetailSearchPlayerResultDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private Player ronaldo24;

    @BeforeEach
    void setUp() {
        ronaldo24 = Player.builder()
                .name("ronaldo")
                .birth(LocalDate.of(1985, 2, 5))
                .nationName("PORTUGAL")
                .playerApiId(7)
                .imageUrl("ronaldoIMG")
                .height(187)
                .weight(96)
                .nationLogoUrl("PORTUGALIMG")
                .mappingStatus(MappingStatus.MATCHED)
                .firstName("Cristiano")
                .lastName("Ronaldo")
                .isGK(false)
                .build();
        Team team = Team.builder().name("Al nasr").build();
        FmPlayer fmPlayer = createFmFieldPlayer(
                1,
                FmVersion.FM24,
                "Cristiano",
                "Ronaldo",
                LocalDate.of(1985, 2, 5),
                "PORTUGAL", 180, 200);
        League league = League.builder().name("saudi").build();
        team.updateLeague(league);
        ronaldo24.updateTeam(team);
        ronaldo24.updateFmPlayer(fmPlayer);
    }

    @Test
    @DisplayName("player 상세 조회 시, playerDetailsDto 반환")
    void getPlayerDetails(){
        // given
        Long playerId = 1L;
        ReflectionTestUtils.setField(ronaldo24, "id", playerId);
        given(playerRepository.findWithTeamLeagueById(playerId)).willReturn(Optional.of(ronaldo24));
        // when
        PlayerDetailsDto result = playerService.getPlayerDetails(playerId);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(ronaldo24.getName());
        assertThat(result.getBirth()).isEqualTo(ronaldo24.getBirth());
        assertThat(result.getNationName()).isEqualTo(ronaldo24.getNationName());
        assertThat(result.getMappingStatus()).isEqualTo(ronaldo24.getMappingStatus());
        assertThat(result.getCurrentAbility()).isEqualTo(ronaldo24.getLatestFmPlayer().getCurrentAbility());
        assertThat(result.getCurrentSeason()).isEqualTo(ronaldo24.getTeam().getLeague().getCurrentSeason());
        assertThat(result.getTeamName()).isEqualTo(ronaldo24.getTeam().getName());
        assertThat(result.getTeamLogoUrl()).isEqualTo(ronaldo24.getTeam().getLogoUrl());
    }

    @Test
    public void testGetPlayerDetailsNotFound() {
        // given
        Long playerId = 1L;
        when(playerRepository.findWithTeamLeagueById(playerId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            playerService.getPlayerDetails(playerId);
        });
        assertTrue(exception.getMessage().contains("Player not found: " + playerId));
    }

    @Test
    public void testGetTeamPlayersByTeamId() {
        // given
        Long teamId = 1L;
        Team team = Team.builder().name("Team A").build();

        Player player1 = Player.builder().build();
        ReflectionTestUtils.setField(player1, "id", 1L);
        ReflectionTestUtils.setField(player1, "team", team);

        Player player2 = Player.builder().build();
        ReflectionTestUtils.setField(player2, "id", 2L);
        ReflectionTestUtils.setField(player2, "team", team);

        when(playerRepository.findAllByTeamId(teamId)).thenReturn(Arrays.asList(player1, player2));

        // when
        TeamPlayersResponseDto result = playerService.getTeamPlayersByTeamId(teamId);

        // then
        assertNotNull(result);
        List<PlayerDetailsDto> players = result.getPlayers();
        assertEquals(2, players.size());
    }

    @Test
    @DisplayName("playerId로 fmPlayer 불러올때 매핑된 fmplayer 가 없으면 null 반환")
    void findFmPlayerDetails_null(){
        // given
        Player player = createPlayer("p1", "p1", LocalDate.now(), "n1",  MappingStatus.UNMAPPED);
        ReflectionTestUtils.setField(player, "id", 1L);
        // when
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        Optional<List<FmPlayerDetailsDto>> result = playerService.findFmPlayerDetails(player.getId());
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("선수 상세 검색시 검색 조건 없으면 모든 player 반환")
    void searchPlayerDetails_valid(){
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Player player1 = createPlayer("p1", "p1", LocalDate.now(), "n1",  MappingStatus.MATCHED);
        Player player2 = createPlayer("p2", "p2", LocalDate.now(), "n2",  MappingStatus.UNMAPPED);
        Player player3 = createPlayer("p3", "p3", LocalDate.now(), "n3",  MappingStatus.FAILED);
        SearchPlayerCondition condition = new SearchPlayerCondition();

        // when
        when(playerRepository.searchPlayerByDetailCondition(condition, pageable))
                .thenReturn(new PageImpl<>(List.of(player1, player2, player3)));
        DetailSearchPlayerResultDto actual = playerService.detailSearchPlayers(condition, pageable);

        // then
        assertThat(actual.getPlayers()).extracting("name")
                .containsExactly(player1.getName(), player2.getName(), player3.getName());

    }


    @Test
    @DisplayName("player 들의 국가 조회 테스트")
    void get_nationNames_from_player(){
        // given
        Player player1 = createPlayer("p1", "p1", LocalDate.now(), "n1",  MappingStatus.MATCHED);
        Player player2 = createPlayer("p2", "p2", LocalDate.now(), "n2",  MappingStatus.UNMAPPED);
        Player player3 = createPlayer("p3", "p3", LocalDate.now(), "n3",  MappingStatus.FAILED);
        List<String> nations = Arrays.asList(player1.getNationName(), player2.getNationName(), player3.getNationName());
        // when
        when(playerRepository.getNationNamesFromPlayers()).thenReturn(nations);
        List<NationDto> actual = playerService.getNationsFromPlayers();

        // then
        assertThat(actual).extracting("nationName")
                .containsOnly("n1", "n2", "n3");
    }

    @Test
    @DisplayName("상세 검색 시 player - fmplayer 가 매핑되었을 경우 검색 결과에 fmplayer top 3 능력치 포함")
    void detail_Search_With_MatchedPlayer_contains_Top3Attributes(){
        // given
        LocalDate birth = LocalDate.now();
        Player player = createPlayer("p1", "p1", birth, "n1",  MappingStatus.MATCHED);
        // top3 능력치 = dribble, pace, vision
        FmPlayer fmPlayer = createFmFieldPlayer(1, FmVersion.FM24, "p1", "p1", birth, "n1", 180, 200);
        player.updateFmPlayer(fmPlayer);
        SearchPlayerCondition condition = new SearchPlayerCondition();
        // when
        Pageable pageable = PageRequest.of(0, 10);
        when(playerRepository.searchPlayerByDetailCondition(condition, pageable))
                .thenReturn(new PageImpl<>(List.of(player)));
        DetailSearchPlayerResultDto actual = playerService
                .detailSearchPlayers(condition, pageable);
        // then
        assertThat(actual.getPlayers().getFirst().getTopAttributes())
                .containsExactlyInAnyOrder("dribbling", "pace", "vision");
    }

    @Test
    @DisplayName("연결된 fmplayer가 여러개일때 다가져와야함")
    void getAllFmPlayers(){
        // given
        Long playerId = 1L;
        ReflectionTestUtils.setField(ronaldo24, "id", playerId);
        FmPlayer ronaldoFM26Player = createFmGKPlayer(
                2,
                FmVersion.FM26,
                "Cristiano",
                "Ronaldo",
                LocalDate.of(1985, 2, 5),
                "PORTUGAL", 170, 200);
        ronaldo24.updateFmPlayer(ronaldoFM26Player);
        // when
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(ronaldo24));
        Optional<List<FmPlayerDetailsDto>> result = playerService.findFmPlayerDetails(playerId);

        // then
        assertThat(ronaldoFM26Player.getPlayer()).isEqualTo(ronaldo24);
        assertThat(ronaldo24.getLatestFmPlayer()).isEqualTo(ronaldoFM26Player);
        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting(FmPlayerDetailsDto::getCurrentAbility)
                .containsExactlyInAnyOrder(180, 170);
        assertThat(result.get())
                .extracting(FmPlayerDetailsDto::getVersion)
                .containsExactlyInAnyOrder(FmVersion.FM24, FmVersion.FM26);
    }

    private Player createPlayer(String firstName, String lastName, LocalDate birth, String nation, MappingStatus mappingStatus) {
        return Player.builder()
                .firstName(firstName)
                .lastName(lastName)
                .name(firstName+lastName)
                .birth(birth)
                .nationName(nation)
                .mappingStatus(mappingStatus)
                .build();
    }

    private FmPlayer createFmFieldPlayer(Integer fmUid, FmVersion fmVersion, String firstName, String lastName, LocalDate birth, String nation, Integer currentAbility, Integer potentialAbility) {
        return FmPlayer.builder()
                .fmUid(1)
                .fmVersion(fmVersion)
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nation)
                .position(Position.builder()
                        .goalkeeper(1)
                        .defenderCentral(1)
                        .defenderLeft(1)
                        .defenderRight(1)
                        .wingBackLeft(1)
                        .wingBackRight(1)
                        .defensiveMidfielder(1)
                        .midfielderLeft(1)
                        .midfielderRight(1)
                        .midfielderCentral(1)
                        .attackingMidCentral(10)
                        .attackingMidLeft(5)
                        .attackingMidRight(8)
                        .striker(20)
                        .build())
                .personalityAttributes(PersonalityAttributes.builder()
                        .adaptability(15)
                        .ambition(18)
                        .loyalty(1)
                        .pressure(17)
                        .professional(19)
                        .sportsmanship(16)
                        .temperament(15)
                        .controversy(3)
                        .build())
                .technicalAttributes(TechnicalAttributes.builder()
                        .corners(14)
                        .crossing(18)
                        .dribbling(20)
                        .finishing(1)
                        .firstTouch(1)
                        .freeKicks(18)
                        .heading(12)
                        .longShots(18)
                        .longThrows(5)
                        .marking(10)
                        .passing(19)
                        .penaltyTaking(18)
                        .tackling(10)
                        .technique(1)
                        .build())
                .mentalAttributes(MentalAttributes.builder()
                        .aggression(10)
                        .anticipation(1)
                        .bravery(15)
                        .composure(18)
                        .concentration(16)
                        .decisions(19)
                        .determination(1)
                        .flair(1)
                        .leadership(13)
                        .offTheBall(1)
                        .positioning(13)
                        .teamwork(17)
                        .vision(20)
                        .workRate(15)
                        .build())
                .physicalAttributes(PhysicalAttributes.builder()
                        .acceleration(19)
                        .agility(19)
                        .balance(1)
                        .jumpingReach(8)
                        .naturalFitness(17)
                        .pace(20)
                        .stamina(17)
                        .strength(13)
                        .build())
                .goalKeeperAttributes(GoalKeeperAttributes.builder()
                        .aerialAbility(1)
                        .commandOfArea(1)
                        .communication(1)
                        .eccentricity(1)
                        .handling(1)
                        .kicking(1)
                        .oneOnOnes(1)
                        .reflexes(1)
                        .rushingOut(1)
                        .tendencyToPunch(1)
                        .throwing(1)
                        .build())
                .hiddenAttributes(HiddenAttributes.builder()
                        .consistency(19)
                        .dirtiness(3)
                        .importantMatches(20)
                        .injuryProneness(8)
                        .versatility(15)
                        .build())
                .currentAbility(currentAbility)
                .potentialAbility(potentialAbility)
                .build();
    }

    private FmPlayer createFmGKPlayer(Integer fmUid, FmVersion fmVersion, String firstName, String lastName, LocalDate birth, String nation, Integer currentAbility, Integer potentialAbility) {
        return FmPlayer.builder()
                .fmUid(fmUid)
                .fmVersion(fmVersion)
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nation)
                .position(Position.builder()
                        .goalkeeper(20)
                        .defenderCentral(1)
                        .defenderLeft(1)
                        .defenderRight(1)
                        .wingBackLeft(1)
                        .wingBackRight(1)
                        .defensiveMidfielder(1)
                        .midfielderLeft(1)
                        .midfielderRight(1)
                        .midfielderCentral(1)
                        .attackingMidCentral(1)
                        .attackingMidLeft(1)
                        .attackingMidRight(1)
                        .striker(1)
                        .build())
                .personalityAttributes(PersonalityAttributes.builder()
                        .adaptability(15)
                        .ambition(18)
                        .loyalty(20)
                        .pressure(17)
                        .professional(19)
                        .sportsmanship(16)
                        .temperament(15)
                        .controversy(3)
                        .build())
                .technicalAttributes(TechnicalAttributes.builder()
                        .corners(1)
                        .crossing(1)
                        .dribbling(1)
                        .finishing(1)
                        .firstTouch(1)
                        .freeKicks(1)
                        .heading(1)
                        .longShots(1)
                        .longThrows(1)
                        .marking(1)
                        .passing(1)
                        .penaltyTaking(1)
                        .tackling(1)
                        .technique(1)
                        .build())
                .mentalAttributes(MentalAttributes.builder()
                        .aggression(10)
                        .anticipation(20)
                        .bravery(15)
                        .composure(18)
                        .concentration(16)
                        .decisions(19)
                        .determination(20)
                        .flair(20)
                        .leadership(13)
                        .offTheBall(20)
                        .positioning(13)
                        .teamwork(17)
                        .vision(20)
                        .workRate(15)
                        .build())
                .physicalAttributes(PhysicalAttributes.builder()
                        .acceleration(19)
                        .agility(19)
                        .balance(20)
                        .jumpingReach(8)
                        .naturalFitness(17)
                        .pace(19)
                        .stamina(17)
                        .strength(13)
                        .build())
                .goalKeeperAttributes(GoalKeeperAttributes.builder()
                        .aerialAbility(20)
                        .commandOfArea(20)
                        .communication(20)
                        .eccentricity(1)
                        .handling(1)
                        .kicking(1)
                        .oneOnOnes(1)
                        .reflexes(1)
                        .rushingOut(1)
                        .tendencyToPunch(1)
                        .throwing(1)
                        .build())
                .hiddenAttributes(HiddenAttributes.builder()
                        .consistency(19)
                        .dirtiness(3)
                        .importantMatches(20)
                        .injuryProneness(8)
                        .versatility(15)
                        .build())
                .currentAbility(currentAbility)
                .potentialAbility(potentialAbility )
                .build();
    }
}
