package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Import({TestQueryDSLConfig.class, TestTimeProviderConfig.class})
@DataJpaTest
class PlayerRepositoryCustomTest {

    private static final int LIMIT = 6;
    private static final Pageable PAGEABLE = PageRequest.of(0, 6);

    @Autowired
    private TestEntityManager tem;

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("검색 시 lastPlayerId or lastMappingStatus null 이면 첫 페이지 반환")
    void search_player_byName_firstPage(){
        // given
        String firstName = "messi";

        createPlayers(firstName);

        // when
        Slice<Player> actual = playerRepository.searchPlayerByName(
                "messi", PAGEABLE,null, null, null);
        // then
        Assertions.assertThat(actual).hasSize(LIMIT);
        Assertions.assertThat(actual.hasNext()).isTrue();
        Assertions.assertThat(actual.getContent())
                .extracting(Player::getMappingStatus)
                .allMatch(status -> status == MappingStatus.MATCHED);
    }

    @Test
    @DisplayName("검색결과 없는 경우 빈 배열 반환")
    void search_no_result(){
        // given
        String name = "messi";
        createPlayers(name);
        // when
        Slice<Player> actual = playerRepository
                .searchPlayerByName("ronaldo", PAGEABLE, null, null, null);

        // then
        Assertions.assertThat(actual.getContent()).isEmpty();
    }

    @Test
    @DisplayName("검색 시 mappingStatus 가 MATCHED, UNMAPPED, FAILED 순으로 검색됨")
    void search_valid_orderByMappingStatus(){
        // given
        String firstName = "messi";
        int totalPlayerSize = 60;
        int totalPage = totalPlayerSize / LIMIT;
        for(int i=0;i<20;i++){
            Player player = Player.builder().firstName(firstName + i).mappingStatus(MappingStatus.MATCHED).build();
            FmPlayer fmPlayer = FmPlayer.builder().firstName(firstName + i).currentAbility(100 + i*10).build();
            player.updateFmPlayer(fmPlayer);
            tem.persist(player);
            tem.persist(fmPlayer);
        }
        for(int i=20;i<40;i++){
            tem.persist(Player.builder().firstName(firstName + i).mappingStatus(MappingStatus.UNMAPPED).build());
        }
        for(int i=40;i<60;i++){
            tem.persist(Player.builder().firstName(firstName + i).mappingStatus(MappingStatus.FAILED).build());
        }
        tem.flush();
        tem.clear();
        List<MappingStatus> actualMappingStatus = new ArrayList<>();
        // when
        Slice<Player> actual = playerRepository.searchPlayerByName(
                firstName, PAGEABLE, null, null, null);
        int actualPage = 0;
        // then
        while(true){
            for(int i=0;i<LIMIT;i++){
                actualMappingStatus.add(actual.getContent().get(i).getMappingStatus());
            }
            actualPage++;
            Assertions.assertThat(actual.getContent()).hasSize(LIMIT);

            Assertions.assertThat(actual.getContent())
                    .extracting(Player::getFirstName)
                    .allMatch(n -> n.startsWith(firstName));
            if (!actual.hasNext()){
                break;
            }
            actual = playerRepository.searchPlayerByName(
                    firstName,
                    PAGEABLE,
                    actual.getContent().getLast().getId(),
                    actual.getContent().getLast().getFmPlayerCurrentAbility(),
                    actual.getContent().getLast().getMappingStatus()
            );
        }
        Assertions.assertThat(actualMappingStatus.subList(0, 20))
                .allMatch(status -> status == MappingStatus.MATCHED);
        Assertions.assertThat(actualMappingStatus.subList(20, 40))
                .allMatch(status -> status == MappingStatus.UNMAPPED);
        Assertions.assertThat(actualMappingStatus.subList(40, 60))
                .allMatch(status -> status == MappingStatus.FAILED);

        Assertions.assertThat(actual.hasNext()).isFalse();
        Assertions.assertThat(actualPage).isEqualTo(totalPage);
    }

    @Test
    @DisplayName("mapping_status = MATCHED 일때 current_ability desc 조회")
    void search_MATCHED_player_by_current_ability(){
        // given
        String name = "messi";
        for(int i=0;i<30;i++){
            Player player = Player.builder().firstName(name + i).mappingStatus(MappingStatus.MATCHED).build();
            FmPlayer fmPlayer = FmPlayer.builder().firstName(name + i).currentAbility(100 + i*10).build();
            player.updateFmPlayer(fmPlayer);
            tem.persist(player);
            tem.persist(fmPlayer);
        }
        tem.flush();
        tem.clear();
        // when
        Slice<Player> actual = playerRepository
                .searchPlayerByName(name, PAGEABLE, null, null, null);
        int totalPlayerSize = 30;
        int totalPage = totalPlayerSize / LIMIT;
        int actualPage = 1;
        // then
        while (actual.hasNext()){
            actualPage++;
            Assertions.assertThat(actual.getContent()).hasSize(LIMIT);

            Assertions.assertThat(actual.getContent())
                    .extracting(Player::getFirstName)
                    .allMatch(n -> n.startsWith(name));

            Assertions.assertThat(actual.getContent()).extracting(Player::getMappingStatus)
                    .allMatch(status -> status == MappingStatus.MATCHED);

            Assertions.assertThat(actual.getContent()).extracting(Player::getFmPlayerCurrentAbility)
                    .isSortedAccordingTo(Comparator.reverseOrder());

            actual = playerRepository.searchPlayerByName(
                    name,
                    PAGEABLE,
                    actual.getContent().getLast().getId(),
                    actual.getContent().getLast().getFmPlayerCurrentAbility(),
                    actual.getContent().getLast().getMappingStatus());

        }
        Assertions.assertThat(actual.hasNext()).isFalse();
        Assertions.assertThat(actualPage).isEqualTo(totalPage);
    }


    @Test
    @DisplayName("상세 검색 시, 최소 나이와 최대 나이의 선수들 검색")
    void detail_search_between_age(){
        // given
        // now - 2000.8.14
        int minAge = 20;
        int maxAge = 30;
        Player ob = Player.builder().firstName("ob").birth(LocalDate.of(1000, 8, 14)).build();
        Player p = Player.builder().firstName("p").birth(LocalDate.of(1980, 8, 14)).build();
        Player yb = Player.builder().firstName("yb").birth(LocalDate.of(3000, 8, 14)).build();

        persistAndFlushPlayers(List.of(ob, p, yb));

        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition();
        searchPlayerCondition.setAgeMin(minAge);
        searchPlayerCondition.setAgeMax(maxAge);

        // when
        Page<Player> actual = playerRepository.searchPlayerByDetailCondition(searchPlayerCondition, PAGEABLE);

        // then
        Assertions.assertThat(actual.getContent()).hasSize(1);
        Assertions.assertThat(actual.getContent().getFirst().getFirstName())
                .isEqualTo(p.getFirstName());
    }

    private void createPlayers(String name){
        List<Player> players = new ArrayList<>();
        for (int i=0;i<LIMIT;i++){
            Player player = Player.builder()
                    .firstName(name + i)
                    .mappingStatus(MappingStatus.MATCHED)
                    .build();
            players.add(player);
        }

        for (int i=LIMIT;i<LIMIT*2;i++){
            Player player = Player.builder()
                    .firstName(name + i)
                    .mappingStatus(MappingStatus.UNMAPPED)
                    .build();
            players.add(player);
        }
        persistAndFlushPlayers(players);
    }


    @Test
    @DisplayName("이름 검색 시 대소문자 무시")
    void simple_search_ignore_case(){
        // given
        Player player1 = Player.builder().firstName("LIONEL").lastName("MESSI").mappingStatus(MappingStatus.UNMAPPED).build();
        Player player2 = Player.builder().firstName("LIONEL").lastName("RONALDO").mappingStatus(MappingStatus.UNMAPPED).build();
        Player player3 = Player.builder().firstName("CRISTIAN").lastName("RONALDO").mappingStatus(MappingStatus.UNMAPPED).build();

        persistAndFlushPlayers(List.of(player1, player2, player3));

        // when
        Slice<Player> actual =
                playerRepository.searchPlayerByName("lio", PAGEABLE, null, null, null);
        // then
        Assertions.assertThat(actual.getContent()).containsExactly(player1, player2);
    }

    private void persistAndFlushPlayers(List<Player> players){
        for (Player player : players){
            tem.persist(player);
        }
        tem.flush();
    }
}