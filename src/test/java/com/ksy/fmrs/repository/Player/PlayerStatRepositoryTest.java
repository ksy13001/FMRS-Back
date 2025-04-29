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
    @DisplayName("PlayerStat 저장 테스트, PlayerStat 저장 전 Player 저장되있어야 하고 PlayerStat 저장 전 Player 에 PlayerStat 값 업데이트해야함")
    void save() {
        // given
        Player player = Player.builder()
                .playerApiId(1)
                .build();
        tem.persistAndFlush(player);

        PlayerStat playerStat = PlayerStat.builder()
                .playerId(player.getId())
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
        player.updatePlayerStat(playerStat);
        PlayerStat savedPlayerStat = playerStatRepository.save(playerStat);

        // then
        Assertions.assertThat(savedPlayerStat.getPlayerId()).isEqualTo(playerStat.getPlayerId());
        Assertions.assertThat(savedPlayerStat.getPlayerId()).isEqualTo(player.getId());
    }
}
