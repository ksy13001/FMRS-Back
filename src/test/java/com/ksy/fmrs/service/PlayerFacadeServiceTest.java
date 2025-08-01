package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.dto.comment.CommentCountResponseDto;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class PlayerFacadeServiceTest {
    @InjectMocks
    private PlayerFacadeService playerFacadeService;
    @Mock
    private PlayerStatService playerStatService;
    @Mock
    private PlayerService playerService;
    @Mock
    private CommentService commentService;

    private PlayerDetailsDto playerDetailsDto;
    private PlayerStatDto playerStatDto;
    private FmPlayerDetailsDto fmPlayerDetailsDto;
    private CommentCountResponseDto commentCountResponseDto;

    private static final int COMMENT_CNT = 10;

    @BeforeEach
    void setUp() {
        LocalDate date = LocalDate.now();
        Player player = Player.builder()
                .playerApiId(1)
                .firstName("firstName")
                .lastName("lastName")
                .birth(date)
                .nationName("nationName")
                .height(180)
                .weight(80)
                .imageUrl("imageUrl")
                .nationLogoUrl("nationLogoUrl")
                .mappingStatus(MappingStatus.MATCHED)
                .build();
        ReflectionTestUtils.setField(player, "id", 1L);
        FmPlayer fmPlayer = createFmPlayer(date);
        PlayerStat playerStat = PlayerStat.builder()
                .build();
        player.updateFmPlayer(fmPlayer);


        this.playerDetailsDto = new PlayerDetailsDto(player, "team1", "teamLogoUrl", 200);
        this.fmPlayerDetailsDto = new FmPlayerDetailsDto(fmPlayer);
        this.playerStatDto = new PlayerStatDto(playerStat);
        this.commentCountResponseDto = new CommentCountResponseDto(COMMENT_CNT);
    }

    @Test
    @DisplayName("playerDetailsDto, playerStatDto, fmPlayerDetailsDto 를 각 서비스에서 받아 dto 통합")
    void getPlayerOverView_valid(){
        // given
        Long playerId = 1L;
        // when
        when(playerService.getPlayerDetails(playerId)).thenReturn(playerDetailsDto);
        when(playerService.getFmPlayerDetails(playerId)).thenReturn(Optional.of(fmPlayerDetailsDto));
        when(playerStatService.saveAndGetPlayerStat(playerId)).thenReturn(Optional.of(playerStatDto));
        when(commentService.getCommentCountByPlayerId(playerId)).thenReturn(commentCountResponseDto);
        PlayerOverviewDto result = playerFacadeService.getPlayerOverview(playerId);
        // then
        Assertions.assertThat(result.fmPlayerDetailsDto()).isEqualTo(fmPlayerDetailsDto);
        Assertions.assertThat(result.playerDetailsDto()).isEqualTo(playerDetailsDto);
        Assertions.assertThat(result.playerStatDto()).isEqualTo(playerStatDto);
        Assertions.assertThat(result.commentCountResponseDto()).isEqualTo(commentCountResponseDto);
    }

    @Test
    @DisplayName("fm 매핑 정보 없을때 PlayerOverviewDto에 fmPlayerDetailsDto = null")
    void getPlayerOverView_fmPlayerDetailsDto_null(){
        // given
        Long playerId = 1L;
        // when
        when(playerService.getPlayerDetails(playerId)).thenReturn(playerDetailsDto);
        when(playerService.getFmPlayerDetails(playerId)).thenReturn(Optional.empty());
        when(playerStatService.saveAndGetPlayerStat(playerId)).thenReturn(Optional.of(playerStatDto));
        when(commentService.getCommentCountByPlayerId(playerId)).thenReturn(commentCountResponseDto);
        PlayerOverviewDto result = playerFacadeService.getPlayerOverview(playerId);
        // then
        Assertions.assertThat(result.fmPlayerDetailsDto()).isNull();
        Assertions.assertThat(result.playerDetailsDto()).isEqualTo(playerDetailsDto);
        Assertions.assertThat(result.playerStatDto()).isEqualTo(playerStatDto);
        Assertions.assertThat(result.commentCountResponseDto()).isEqualTo(commentCountResponseDto);
    }

    private FmPlayer createFmPlayer(LocalDate date) {
        return FmPlayer.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birth(date)
                .nationName("nationName")
                .position(Position.builder()
                        .goalkeeper(0)
                        .defenderCentral(0)
                        .defenderLeft(0)
                        .defenderRight(0)
                        .wingBackLeft(0)
                        .wingBackRight(0)
                        .defensiveMidfielder(0)
                        .midfielderLeft(0)
                        .midfielderRight(0)
                        .midfielderCentral(0)
                        .attackingMidCentral(10)
                        .attackingMidLeft(5)
                        .attackingMidRight(8)
                        .striker(20)
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
                        .corners(14)
                        .crossing(18)
                        .dribbling(20)
                        .finishing(20)
                        .firstTouch(20)
                        .freeKicks(18)
                        .heading(12)
                        .longShots(18)
                        .longThrows(5)
                        .marking(10)
                        .passing(19)
                        .penaltyTaking(18)
                        .tackling(10)
                        .technique(20)
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
                .currentAbility(190)
                .potentialAbility(195)
                .build();

    }
}