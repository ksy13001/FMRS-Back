package com.ksy.fmrs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ksy.fmrs.domain.Nation;
import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.PlayerDetailsDto;
import com.ksy.fmrs.dto.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.TeamPlayersResponseDto;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // @Value로 주입받는 값들을 강제로 설정
        ReflectionTestUtils.setField(playerService, "apiFootballKey", "dummyKey");
        ReflectionTestUtils.setField(playerService, "apiFootballHost", "dummyHost");
    }

    @Test
    public void testGetPlayerDetailsSuccess() {
        // given
        Long playerId = 1L;
        // 팀은 빌더를 통해 생성 (name만 인자로)
        Team team = Team.builder().name("Team A").build();
        // Nation은 일반 인스턴스로 생성 (혹은 builder가 있다면 동일하게 사용)
        Nation nation = Nation.builder().name("Nation A").build();

        // Player는 빌더 패턴으로 생성한 후 id, team, nation은 Reflection을 통해 주입
        Player player = Player.builder().build();
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(player, "team", team);
        ReflectionTestUtils.setField(player, "nation", nation);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        // when
        PlayerDetailsDto result = playerService.getPlayerDetails(playerId);

        // then
        assertNotNull(result);
        // 내부 DTO 변환 시 getNationNameByPlayer(player) 는 팀의 이름, getTeamNameByPlayer(player)는 nation의 이름 반환
        assertEquals("Team A", result.getNationName());
        assertEquals("Nation A", result.getTeamName());
    }

    @Test
    public void testGetPlayerDetailsNotFound() {
        // given
        Long playerId = 1L;
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.getPlayerDetails(playerId);
        });
        assertTrue(exception.getMessage().contains("Player not found: " + playerId));
    }

    @Test
    public void testGetTeamPlayersByTeamId() {
        // given
        Long teamId = 1L;
        Team team = Team.builder().name("Team A").build();
        Nation nation = Nation.builder().name("Nation A").build();

        Player player1 = Player.builder().build();
        ReflectionTestUtils.setField(player1, "id", 1L);
        ReflectionTestUtils.setField(player1, "team", team);
        ReflectionTestUtils.setField(player1, "nation", nation);

        Player player2 = Player.builder().build();
        ReflectionTestUtils.setField(player2, "id", 2L);
        ReflectionTestUtils.setField(player2, "team", team);
        ReflectionTestUtils.setField(player2, "nation", nation);

        when(playerRepository.findAllByTeamId(teamId)).thenReturn(Arrays.asList(player1, player2));

        // when
        TeamPlayersResponseDto result = playerService.getTeamPlayersByTeamId(teamId);

        // then
        assertNotNull(result);
        List<PlayerDetailsDto> players = result.getPlayers();
        assertEquals(2, players.size());
    }

    @Test
    public void testGetPlayersByMarketValueDesc() {
        // given
        Nation nation = Nation.builder().name("Nation A").build();
        Team team = Team.builder().name("Team A").build();

        Player player1 = Player.builder().build();
        ReflectionTestUtils.setField(player1, "id", 1L);
        ReflectionTestUtils.setField(player1, "team", team);
        ReflectionTestUtils.setField(player1, "nation", nation);

        Player player2 = Player.builder().build();
        ReflectionTestUtils.setField(player2, "id", 2L);
        ReflectionTestUtils.setField(player2, "team", team);
        ReflectionTestUtils.setField(player2, "nation", nation);

        when(playerRepository.findAllByOrderByMarketValueDesc()).thenReturn(Arrays.asList(player1, player2));

        // when
        SearchPlayerResponseDto result = playerService.getPlayersByMarketValueDesc();

        // then
        assertNotNull(result);
        assertEquals(2, result.getPlayers().size());
    }

    @Test
    public void testSearchPlayerByName() {
        // given
        String name = "John";
        Nation nation = Nation.builder().name("Nation A").build();
        Team team = Team.builder().name("Team A").build();

        Player player = Player.builder().build();
        ReflectionTestUtils.setField(player, "id", 1L);
        ReflectionTestUtils.setField(player, "team", team);
        ReflectionTestUtils.setField(player, "nation", nation);

        when(playerRepository.searchPlayerByName(name)).thenReturn(Arrays.asList(player));

        // when
        SearchPlayerResponseDto result = playerService.searchPlayerByName(name);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPlayers().size());
    }

    @Test
    public void testSearchPlayerByDetailCondition() {
        // given
        SearchPlayerCondition condition = new SearchPlayerCondition();
        // 필요한 조건 필드가 있다면 condition에 추가 설정
        Nation nation = Nation.builder().name("Nation A").build();
        Team team = Team.builder().name("Team A").build();

        Player player = Player.builder().build();
        ReflectionTestUtils.setField(player, "id", 1L);
        ReflectionTestUtils.setField(player, "team", team);
        ReflectionTestUtils.setField(player, "nation", nation);

        when(playerRepository.searchPlayerByDetailCondition(condition)).thenReturn(Arrays.asList(player));

        // when
        SearchPlayerResponseDto result = playerService.searchPlayerByDetailCondition(condition);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPlayers().size());
    }
}
