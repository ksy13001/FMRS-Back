package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.QPlayer;
import com.ksy.fmrs.domain.QTeam;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.SearchPlayerCondition;
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
    @DisplayName("상세 검색 테스트 - 팀")
    void detail_search_test(){
        // given
        SearchPlayerCondition condition = new SearchPlayerCondition();
        condition.setTeamName("TOT");
        Team team = Team.builder().name("TOT").build();
        Player player = Player.builder().name("son").age(32).build();
        teamRepository.save(team);
        playerRepository.save(player);
        player.updateTeam(player, team);

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
}