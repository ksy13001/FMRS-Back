package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestQueryDSLConfig.class)
@DataJpaTest
class PlayerStatRepositoryTest {

    @Autowired
    private PlayerStatRepository playerStatRepository;
    @Autowired
    private TestEntityManager tem;

    @Test
    @DisplayName("PlayerStat 저장 테스트")
    void save() {
        // given
        Player player = Player.builder()
                .playerApiId(1)
                .build();
        tem.persistAndFlush(player);

        PlayerStat playerStat = PlayerStat.builder()
                .player(player)
                .gamesPlayed(10)
                .substitutes(5)
                .goal(15)
                .assist(10)
                .pk(3)
                .rating("7.15")
                .yellowCards(5)
                .redCards(3)
                .build();

        // when
        PlayerStat saved = playerStatRepository.save(playerStat);

        // then
        assertAll("saved PlayerStat",
                () ->  Assertions.assertThat(saved.getPlayerId()).isEqualTo(player.getId()),
                () ->  Assertions.assertThat(saved.getPlayer().getId()).isEqualTo(player.getId()),
                () ->  Assertions.assertThat(saved.getGamesPlayed()).isEqualTo(10),
                () ->  Assertions.assertThat(saved.getSubstitutes()).isEqualTo(5),
                () ->  Assertions.assertThat(saved.getGoal()).isEqualTo(15),
                () ->  Assertions.assertThat(saved.getAssist()).isEqualTo(10),
                () ->  Assertions.assertThat(saved.getPk()).isEqualTo(3),
                () ->  Assertions.assertThat(saved.getRating()).isEqualTo("7.15"),
                () ->  Assertions.assertThat(saved.getYellowCards()).isEqualTo(5),
                () ->  Assertions.assertThat(saved.getRedCards()).isEqualTo(3)
        );
//
//        tem.flush();
//        tem.clear();
//        Optional<PlayerStat> opt = playerStatRepository.findById(player.getId());
//        assertThat(opt).isPresent();
    }
}
