package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


@Import(TestQueryDSLConfig.class)
@DataJpaTest
class PlayerRepositoryCustomTest {

    private static final int TOTAL = 10;
    @Autowired
    private TestEntityManager tem;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @DisplayName("검색 시 lastPlayerId or lastMappingStatus null 이면 첫 페이지 반환")
    void search_player_byName_firstPage(){
        // given
        int limit = 5;
        Pageable pageable = PageRequest.of(0, limit);

        createPlayers();

        // when
        Slice<Player> actual = playerRepository.searchPlayerByName("messi", pageable,null, null);
        // then
        Assertions.assertThat(actual).hasSize(limit);
        Assertions.assertThat(actual.hasNext()).isTrue();
        Assertions.assertThat(actual.getContent())
                .extracting(Player::getMappingStatus)
                .allMatch(status -> status == MappingStatus.MATCHED);
    }

    @Test
    @DisplayName("검색 시 mappingStatus 가 MATCHED, UNMAPPED, FAILED 순으로 검색됨")
    void search_valid_orderByMappingStatus(){
        // given
        Player player1 = Player.builder().name("messi1").mappingStatus(MappingStatus.FAILED).build();
        Player player2 = Player.builder().name("messi2").mappingStatus(MappingStatus.UNMAPPED).build();
        Player player3 = Player.builder().name("messi3").mappingStatus(MappingStatus.MATCHED).build();
        tem.persist(player1);
        tem.persist(player2);
        tem.persist(player3);
        tem.flush();
        // when
        Pageable pageable = PageRequest.of(0, 5);
        Slice<Player> actual = playerRepository.searchPlayerByName(
                "messi", pageable, null, null);
        // then
        Assertions.assertThat(actual).hasSize(3);
        Assertions.assertThat(actual.hasNext()).isFalse();
        Assertions.assertThat(actual.getContent())
                .extracting(Player::getMappingStatus)
                .containsExactly(MappingStatus.MATCHED, MappingStatus.UNMAPPED,  MappingStatus.FAILED);

    }

    private void createPlayers(){
        for (int i=0;i<5;i++){
            Player player = Player.builder()
                    .name("messi" + i)
                    .mappingStatus(MappingStatus.MATCHED)
                    .build();
            tem.persist(player);
        }

        for (int i=5;i<10;i++){
            Player player = Player.builder()
                    .name("messi" + i)
                    .mappingStatus(MappingStatus.UNMAPPED)
                    .build();
            tem.persist(player);
        }

        tem.flush();
    }
}