package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.QPlayer;
import com.ksy.fmrs.domain.QTeam;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // default 는 h2 사용
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @TestConfiguration
     static class QueryDslTestConfig {
        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @BeforeEach
    void setUp(){
        playerRepository.deleteAll();
    }

    @Test
    @DisplayName("팀id로 소속 선수들 조회")
    void getPlayersByTeamId(){
        // given
        ArrayList<Player> players = new ArrayList<>();
        Team team1 = createTeam("team1");
        Team team2 = createTeam("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);
        for(int i = 0; i < 10; i++){
            Player player = createPlayer("player"+i);
            players.add(player);
            player.updateTeam(team1);
            playerRepository.save(player);
        }
        for(int i = 0; i < 5; i++){
            Player player = createPlayer("not_player"+i);
            players.add(player);
            player.updateTeam(team2);
            playerRepository.save(player);
        }
        // when
        List<Player> actual = playerRepository.findAllByTeamId(team1.getId());
        // then
        actual.forEach(player -> {
            Assertions.assertThat(player.getName()).startsWith("player");
        });
        Assertions.assertThat(actual).hasSize(10);
    }

    @Test
    @DisplayName("상세 검색 테스트 - 팀")
    void detail_search_test(){
        // given
        SearchPlayerCondition condition = new SearchPlayerCondition();
        condition.setTeamName("TOT");
        Team team = Team.builder().name("TOT").build();
        Player player = Player.builder().name("son").age(32).build();
        teamRepository.save(team);
        playerRepository.save(player);
        player.updateTeam(team);

        // when
        List<Player> result = playerRepository.searchPlayerByDetailCondition(condition);
        List<Player> expected = jpaQueryFactory
                .selectFrom(QPlayer.player)
                .leftJoin(QPlayer.player.team, QTeam.team)
                .where(
                        QTeam.team.name.eq(condition.getTeamName())
                )
                .fetch();

        // then
        Assertions.assertThat(result.get(0).getName()).isEqualTo(expected.get(0).getName());
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("몸값 순 선수 모두 조회")
    void findAllOrderByMarketValueDesc(){
        // given
        Player playerA = createPlayer("playerA");
        playerA.updateMarketValue(100);
        Player playerB = createPlayer("playerB");
        playerB.updateMarketValue(200);
        Player playerC = createPlayer("playerC");
        playerC.updateMarketValue(300);
        playerRepository.save(playerA);
        playerRepository.save(playerB);
        playerRepository.save(playerC);

        // when
        List<Player> actual = playerRepository.findAllByOrderByMarketValueDesc();
        // then
        Assertions.assertThat(actual).hasSize(3);
        Assertions.assertThat(actual.get(0).getName()).isEqualTo("playerC");
        Assertions.assertThat(actual.get(1).getName()).isEqualTo("playerB");
        Assertions.assertThat(actual.get(2).getName()).isEqualTo("playerA");
    }

    private Team createTeam(String name){
        return Team.builder().name(name).build();
    }

    private Player createPlayer(String name) {
        return Player.builder().name(name).build();
    }
}