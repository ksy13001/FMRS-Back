package com.ksy.fmrs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.nation.NationDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PlayerService playerService;

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
//        Nation nation = Nation.builder().name("Nation A").build();

        Player player1 = Player.builder().build();
        ReflectionTestUtils.setField(player1, "id", 1L);
        ReflectionTestUtils.setField(player1, "team", team);
//        ReflectionTestUtils.setField(player1, "nation", nation);

        Player player2 = Player.builder().build();
        ReflectionTestUtils.setField(player2, "id", 2L);
        ReflectionTestUtils.setField(player2, "team", team);
//        ReflectionTestUtils.setField(player2, "nation", nation);

        when(playerRepository.findAllByTeamId(teamId)).thenReturn(Arrays.asList(player1, player2));

        // when
        TeamPlayersResponseDto result = playerService.getTeamPlayersByTeamId(teamId);

        // then
        assertNotNull(result);
        List<PlayerDetailsDto> players = result.getPlayers();
        assertEquals(2, players.size());
    }

    @Test
    @DisplayName("이름 한 단어인 경우 테스트")
    void oneNameTest(){
        // given
        String name = "ronaldo";
        // when
        String lastName = StringUtils.getLastName(name);
        String firstName = StringUtils.getFirstName(name);
        // then
        Assertions.assertThat("ronaldo").isEqualTo(firstName);
        Assertions.assertThat("ronaldo").isEqualTo(lastName);
    }


    @Test
    @DisplayName("파싱 테스트")
    void parseName(){
        // given
        String name = "K. De Bruyne";
        String firstname = "Kevin";
        String lastname = "De Bruyne";

        Assertions.assertThat("Kevin").isEqualTo(StringUtils.getFirstName(firstname));
        Assertions.assertThat("Bruyne").isEqualTo(StringUtils.getLastName(name));
    }

    @Test
    @DisplayName("player 에 대응되는 fmplayer가 2개 이상일때 해당 player들 mapping_status = FAILED 처리")
    void updatePlayersWithMultipleFmPlayersTOFailed(){
        // given
        String firstname = "KEVIN";
        String lastname = "DE BRUYNE";
        LocalDate now = LocalDate.now();
        String nationName = "BELGIUM";
        Player player1 =createPlayer(firstname, lastname, now, nationName, MappingStatus.UNMAPPED);
        Player player2 =createPlayer(firstname, lastname, now, nationName, MappingStatus.UNMAPPED);
        Player player3 =createPlayer(firstname, lastname, now, nationName, MappingStatus.UNMAPPED);

        List<Player> players = Arrays.asList(player1, player2, player3);
        // when
        playerService.updatePlayersMappingStatusToFailed(players);

        // then
        Assertions.assertThat(players)
                .extracting(Player::getMappingStatus)
                .containsOnly(MappingStatus.FAILED);
    }

    @Test
    @DisplayName("playerId로 fmPlayer 불러올때 매핑된 fmplayer 가 없으면 null 반환")
    void getFmPlayerDetails_null(){
        // given
        Player player = createPlayer("p1", "p1", LocalDate.now(), "n1",  MappingStatus.UNMAPPED);
        ReflectionTestUtils.setField(player, "id", 1L);
        // when
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        Optional<FmPlayerDetailsDto> result = playerService.getFmPlayerDetails(player.getId());
        // then
        Assertions.assertThat(result).isEmpty();
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
        SearchPlayerResponseDto actual = playerService.searchPlayerByDetailCondition(condition, pageable);

        // then
        Assertions.assertThat(actual.getPlayers()).extracting("name")
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
        Assertions.assertThat(actual).extracting("nationName")
                .containsOnly("n1", "n2", "n3");
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

    private FmPlayer createFmPlayer(String firstName, String lastName, LocalDate birth, String nation) {
        return FmPlayer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nation)
                .build();
    }
}
