package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.util.StringUtils;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Import({TestQueryDSLConfig.class, TestTimeProviderConfig.class})
@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TestEntityManager tem;

    @Test
    @DisplayName("save 단건")
    void save() {
        // given
        Player player = createPlayer("p1", "p1", LocalDate.now(), "n1", MappingStatus.UNMAPPED);
        // when
        Player savePlayer = playerRepository.save(player);

        // then
        Assertions.assertThat(savePlayer).isEqualTo(player);
    }

    @Test
    @DisplayName("saveAll 시 select 문 나가는지 테스트")
    void saveAll() {
        // given
        List<Player> playerList = new ArrayList<>();
        Player player = createPlayer("p1", "p1", LocalDate.now(), "n1", MappingStatus.UNMAPPED);
        Player player2 = createPlayer("p2", "p2", LocalDate.now(), "n2", MappingStatus.UNMAPPED);
        Team team1 = createTeam("t1");
        Team team2 = createTeam("t2");
        player.updateTeam(team1);
        player2.updateTeam(team2);

        playerList.add(player);
        playerList.add(player2);
        // when
        teamRepository.save(team1);
        teamRepository.save(team2);
        playerRepository.saveAll(playerList);

        // then
    }

    @Test
    @DisplayName("팀id로 소속 선수들 조회")
    void getPlayersByTeamId() {
        // given
        ArrayList<Player> players = new ArrayList<>();
        Team team1 = createTeam("team1");
        Team team2 = createTeam("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);
        for (int i = 0; i < 10; i++) {
            Player player = createPlayer("player" + i, "p" + i, LocalDate.now(), "n" + i, MappingStatus.UNMAPPED);
            players.add(player);
            player.updateTeam(team1);
            playerRepository.save(player);
        }
        for (int i = 0; i < 5; i++) {
            Player player = createPlayer("not_player" + i, " p" + i, LocalDate.now(), "n" + i, MappingStatus.UNMAPPED);
            players.add(player);
            player.updateTeam(team2);
            playerRepository.save(player);
        }
        // when
        List<Player> actual = playerRepository.findAllByTeamId(team1.getId());
        // then
//        actual.forEach(player -> {
//            Assertions.assertThat(player.getName()).startsWith("player");
//        });
        Assertions.assertThat(actual).hasSize(10);
    }

    @Test
    @DisplayName("상세 검색 테스트 - 팀")
    void detail_search_test() {

    }

    @Test
    @DisplayName("fmplayerStat으로 player 찾기")
    void searchPlayerByFm() {
        // given
        String fileName = "98031331-Manuel Akanji";
        String name = StringUtils.getPlayerNameFromFileName(fileName);
        String firstName = StringUtils.getFirstName(name).toUpperCase();
        String lastName = StringUtils.getLastName(name).toUpperCase();
        LocalDate birthDate = LocalDate.of(1995, 7, 19);
        String Nation = "Switzerland".toUpperCase();
        Player player = Player.builder()
                .firstName("MANUEL")
                .lastName("AKANJI")
                .nationName("SWITZERLAND")
                .birth(LocalDate.of(1995, 7, 19))
                .build();
        playerRepository.save(player);
        // when
        List<Player> result = playerRepository.searchPlayerByFm(firstName, lastName, birthDate, Nation);

        // then
        Assertions.assertThat(result).hasSize(1);
        Player actual = result.get(0);
        Assertions.assertThat(actual.getFirstName()).isEqualTo("MANUEL");
        Assertions.assertThat(actual.getLastName()).isEqualTo("AKANJI");
        Assertions.assertThat(actual.getNationName()).isEqualTo("SWITZERLAND");
        Assertions.assertThat(actual.getBirth()).isEqualTo(LocalDate.of(1995, 7, 19));

    }

    @Test
    @DisplayName("하나에 player에 매핑되는 fm player가 2명 이상인 player 조회 테스트")
    void findDuplicatedFmPlayer() {
        // given
        FmPlayer fmPlayer1 = FmPlayer.builder()
                .firstName("MANUEL")
                .lastName("AKANJI")
                .nationName("SWITZERLAND")
                .birth(LocalDate.of(1995, 7, 19))
                .build();
        FmPlayer fmPlayer2 = FmPlayer.builder()
                .firstName("MANUEL")
                .lastName("AKANJI")
                .nationName("SWITZERLAND")
                .birth(LocalDate.of(1995, 7, 19))
                .build();
        Player player = Player.builder()
                .firstName("MANUEL")
                .lastName("AKANJI")
                .nationName("SWITZERLAND")
                .birth(LocalDate.of(1995, 7, 19))
                .mappingStatus(MappingStatus.UNMAPPED)
                .build();
        tem.persist(player);
        tem.persist(fmPlayer1);
        tem.persist(fmPlayer2);

        tem.flush();
        // when
        List<Player> players = playerRepository.findPlayerDuplicatedWithFmPlayer();
        List<Player> playerAll = playerRepository.findAll();
        // then
        Assertions.assertThat(playerAll).hasSize(1);
        Assertions.assertThat(players).hasSize(1);
        Assertions.assertThat(players.get(0)).isEqualTo(player);
    }

    @Test
    @DisplayName("매핑 조건이 겹치는 player 조회")
    void findDuplicatedPlayers() {
        // given
        String firstName = "MANUEL";
        String lastName = "AKANJI";
        String nationName = "SWITZERLAND";
        LocalDate birth = LocalDate.of(1995, 7, 19);
        for (int i = 0; i < 100; i++) {
            Player player = createPlayer(firstName, lastName, birth, nationName, MappingStatus.UNMAPPED);
            tem.persist(player);
        }
        for (int i = 0; i < 100; i++) {
            Player player = createPlayer("f" + i, "l" + i, LocalDate.now(), "n" + i, MappingStatus.UNMAPPED);
            tem.persist(player);
        }
        tem.flush();
        tem.clear();
        // when
        List<Player> players = playerRepository.findDuplicatedPlayers();
        // then
        Assertions.assertThat(players).hasSize(100);
        Assertions.assertThat(players).allSatisfy(p -> {
                    Assertions.assertThat(p.getFirstName()).isEqualTo(firstName);
                    Assertions.assertThat(p.getLastName()).isEqualTo(lastName);
                    Assertions.assertThat(p.getNationName()).isEqualTo(nationName);
                    Assertions.assertThat(p.getBirth()).isEqualTo(birth);
                }
        );
    }

    private Team createTeam(String name) {
        return Team.builder().name(name).build();
    }

    private Player createPlayer(String firstName, String lastName, LocalDate birth, String nationName, MappingStatus mappingStatus) {
        return Player.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nationName)
                .mappingStatus(mappingStatus)
                .build();
    }
}