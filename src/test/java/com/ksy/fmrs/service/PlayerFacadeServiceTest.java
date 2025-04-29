package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.PlayerOverviewDto;
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
    PlayerFacadeService playerFacadeService;
    @Mock PlayerStatService playerStatService;
    @Mock PlayerService playerService;

    private PlayerDetailsDto playerDetailsDto;
    private PlayerStatDto playerStatDto;
    private FmPlayerDetailsDto fmPlayerDetailsDto;

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
                .mappingStatus(PlayerMappingStatus.MATCHED)
                .build();
        ReflectionTestUtils.setField(player, "id", 1L);
        FmPlayer fmPlayer = FmPlayer.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birth(date)
                .nationName("nationName")
                .build();
        PlayerStat playerStat = PlayerStat.builder()
                .build();
        player.updateFmPlayer(fmPlayer);
        player.updateFmPlayer(fmPlayer);


        this.playerDetailsDto = new PlayerDetailsDto(player, "team1");
        this.fmPlayerDetailsDto = new FmPlayerDetailsDto(fmPlayer);
        this.playerStatDto = new PlayerStatDto(playerStat);
    }

    @Test
    @DisplayName("playerDetailsDto, playerStatDto, fmPlayerDetailsDto 를 각 서비스에서 받아 dto 통합")
    void getPlayerOverView_valid(){
        // given
        Long playerId = 1L;
        // when
        when(playerService.getPlayerDetails(playerId)).thenReturn(playerDetailsDto);
        when(playerService.getFmPlayerDetails(playerId)).thenReturn(Optional.of(fmPlayerDetailsDto));
        when(playerStatService.getPlayerStatByPlayerId(playerId)).thenReturn(playerStatDto);
        PlayerOverviewDto result = playerFacadeService.getPlayerOverview(playerId);
        // then
        Assertions.assertThat(result.fmPlayerDetailsDto()).isEqualTo(fmPlayerDetailsDto);
        Assertions.assertThat(result.playerDetailsDto()).isEqualTo(playerDetailsDto);
        Assertions.assertThat(result.playerStatDto()).isEqualTo(playerStatDto);
    }

    @Test
    @DisplayName("fm 매핑 정보 없을때 PlayerOverviewDto에 fmPlayerDetailsDto = null")
    void getPlayerOverView_fmPlayerDetailsDto_null(){
        // given
        Long playerId = 1L;
        // when
        when(playerService.getPlayerDetails(playerId)).thenReturn(playerDetailsDto);
        when(playerService.getFmPlayerDetails(playerId)).thenReturn(Optional.empty());
        when(playerStatService.getPlayerStatByPlayerId(playerId)).thenReturn(playerStatDto);
        PlayerOverviewDto result = playerFacadeService.getPlayerOverview(playerId);
        // then
        Assertions.assertThat(result.fmPlayerDetailsDto()).isNull();
        Assertions.assertThat(result.playerDetailsDto()).isEqualTo(playerDetailsDto);
        Assertions.assertThat(result.playerStatDto()).isEqualTo(playerStatDto);
    }
}