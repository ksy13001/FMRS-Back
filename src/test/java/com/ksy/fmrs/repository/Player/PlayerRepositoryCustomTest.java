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

import static org.junit.jupiter.api.Assertions.*;

@Import(TestQueryDSLConfig.class)
@DataJpaTest
class PlayerRepositoryCustomTest {

    private static final int TOTAL = 10;
    @Autowired
    private TestEntityManager tem;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @DisplayName("검색 시 lastPlayerId or lastMappingStatus null 이면 첫 페이지 반환" +
            ", mappingStatus=MATCHED, UNMAPPED, FAILED 순으로 정렬")
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