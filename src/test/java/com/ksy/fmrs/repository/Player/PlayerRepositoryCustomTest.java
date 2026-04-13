package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.exception.InvalidSearchConditionException;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
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
    private static final LocalDate TEST_TODAY = LocalDate.of(2000, 8, 14);

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
        persistMatchedPlayers(firstName, 0, 20);
        persistPlayersWithStatus(firstName, 20, 40, MappingStatus.UNMAPPED);
        persistPlayersWithStatus(firstName, 40, 60, MappingStatus.FAILED);
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
        persistMatchedPlayers(name, 0, 30);
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
    @DisplayName("상세 검색 시 최소/최대 나이 경계를 포함해 조회한다")
    void detail_search_between_age(){
        // given
        int minAge = 20;
        int maxAge = 30;
        Player oldestIncluded = buildPlayer("oldestIncluded", LocalDate.of(1969, 8, 15));
        Player tooOld = buildPlayer("tooOld", LocalDate.of(1969, 8, 14));
        Player youngestIncluded = buildPlayer("youngestIncluded", LocalDate.of(1980, 8, 14));
        Player tooYoung = buildPlayer("tooYoung", LocalDate.of(1980, 8, 15));

        persistAndFlushPlayers(List.of(oldestIncluded, tooOld, youngestIncluded, tooYoung));

        Page<Player> actual = searchPlayerByAge(minAge, maxAge);

        // then
        assertThat(actual.getContent())
                .extracting(Player::getFirstName)
                .containsExactlyInAnyOrder("oldestIncluded", "youngestIncluded");
    }

    @Test
    @DisplayName("상세 검색 시 ageMin 만 주어지면 해당 나이 이상만 조회한다")
    void detail_search_with_only_age_min() {
        // given
        Player exactMinAge = buildPlayer("exactMinAge", TEST_TODAY.minusYears(20));
        Player olderThanMin = buildPlayer("olderThanMin", LocalDate.of(1970, 1, 1));
        Player tooYoung = buildPlayer("tooYoung", TEST_TODAY.minusYears(20).plusDays(1));

        persistAndFlushPlayers(List.of(exactMinAge, olderThanMin, tooYoung));

        // when
        Page<Player> actual = searchPlayerByAge(20, null);

        // then
        assertThat(actual.getContent())
                .extracting(Player::getFirstName)
                .containsExactlyInAnyOrder("exactMinAge", "olderThanMin");
    }

    @Test
    @DisplayName("상세 검색 시 ageMax 만 주어지면 해당 나이 이하만 조회한다")
    void detail_search_with_only_age_max() {
        // given
        Player exactMaxAge = buildPlayer("exactMaxAge", LocalDate.of(1969, 8, 15));
        Player youngerThanMax = buildPlayer("youngerThanMax", LocalDate.of(1985, 1, 1));
        Player tooOld = buildPlayer("tooOld", LocalDate.of(1969, 8, 14));

        persistAndFlushPlayers(List.of(exactMaxAge, youngerThanMax, tooOld));

        // when
        Page<Player> actual = searchPlayerByAge(null, 30);

        // then
        assertThat(actual.getContent())
                .extracting(Player::getFirstName)
                .containsExactlyInAnyOrder("exactMaxAge", "youngerThanMax");
    }

    @Test
    @DisplayName("상세 검색 시 ageMin 이 ageMax 보다 크면 예외가 발생한다")
    void detail_search_invalid_age_range() {
        // given
        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition();
        searchPlayerCondition.setAgeMin(31);
        searchPlayerCondition.setAgeMax(30);

        // when // then
        Assertions.assertThatThrownBy(() ->
                playerRepository.searchPlayerByDetailCondition(searchPlayerCondition, PAGEABLE))
                .isInstanceOf(InvalidSearchConditionException.class)
                .hasMessage("ageMin must be <= ageMax");
    }

    private void createPlayers(String name){
        List<Player> players = new ArrayList<>();
        players.addAll(buildPlayers(name, 0, LIMIT, MappingStatus.MATCHED));
        players.addAll(buildPlayers(name, LIMIT, LIMIT * 2, MappingStatus.UNMAPPED));
        persistAndFlushPlayers(players);
    }

    private void persistAndFlushPlayers(List<Player> players){
        for (Player player : players){
            tem.persist(player);
        }
        tem.flush();
    }

    private List<Player> buildPlayers(String name, int startInclusive, int endExclusive, MappingStatus status) {
        List<Player> players = new ArrayList<>();
        for (int i = startInclusive; i < endExclusive; i++) {
            players.add(Player.builder()
                    .firstName(name + i)
                    .mappingStatus(status)
                    .build());
        }
        return players;
    }

    private void persistPlayersWithStatus(String name, int startInclusive, int endExclusive, MappingStatus status) {
        for (int i = startInclusive; i < endExclusive; i++) {
            tem.persist(Player.builder()
                    .firstName(name + i)
                    .mappingStatus(status)
                    .build());
        }
    }

    private Player buildPlayer(String firstName, LocalDate birth) {
        return Player.builder()
                .firstName(firstName)
                .birth(birth)
                .build();
    }

    private Page<Player> searchPlayerByAge(Integer ageMin, Integer ageMax) {
        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition();
        searchPlayerCondition.setAgeMin(ageMin);
        searchPlayerCondition.setAgeMax(ageMax);
        return playerRepository.searchPlayerByDetailCondition(searchPlayerCondition, PAGEABLE);
    }

    /**
     * Bug-1: FM24+FM26 보유 선수가 LIMIT 슬롯을 2개 차지해 다음 페이지 선수 누락
     *
     * 재현 조건:
     *   - pageSize=3, 실제 선수 4명 (hasNext=true 여야 함)
     *   - Player A가 FM24+FM26 → SQL에서 2행 차지
     *
     * 기대 동작: hasNext = true (4번째 선수 존재)
     * 버그 동작: SQL LIMIT=4 에서 A 2행 + B + C = 4행 → D 잘림
     *           Hibernate dedup 후 [A,B,C] size=3 → hasNext = false (선수 누락)
     *
     * 참고: Hibernate 6은 entity 레벨에서 중복을 자동 제거하므로
     *       "hasNext=true 오판(Case A)"은 발생하지 않는다.
     *       실제 버그는 "다음 페이지 선수 누락(Case B)" 형태로 발생한다.
     */
    @Test
    @DisplayName("FM24+FM26 보유 선수가 LIMIT 슬롯 2개 차지해 다음 페이지 선수 누락")
    void bug1_next_page_player_must_not_be_omitted_when_player_has_multiple_fm_versions() {
        // given — 4명, hasNext=true 여야 함
        int pageSize = 3;
        Pageable pageable = PageRequest.of(0, pageSize);

        // Player A: FM26(ca=200) + FM24(ca=150) — SQL 슬롯 2개 차지
        persistMatchedPlayerWithTwoFmVersions("messi0", 1, 150, 200);
        // Player B, C, D: FM24 1개씩 (ca=120, 110, 100)
        persistMatchedPlayers("messi", 1, 4);
        tem.flush();
        tem.clear();

        // when
        Slice<Player> result = playerRepository.searchPlayerByName(
                "messi", pageable, null, null, null);

        // then
        assertThat(result.getContent())
                .hasSize(pageSize)
                .extracting(Player::getId)
                .doesNotHaveDuplicates();
        assertThat(result.hasNext())
                .as("4번째 선수(D)가 있으므로 hasNext=true 여야 함. " +
                    "A의 FM24 행이 LIMIT 슬롯을 소비해 D가 잘리면 false 반환 (선수 누락)")
                .isTrue();
    }

    /**
     * Bug-2: FM24+FM26 보유 MATCHED 선수가 커서 페이지네이션에서 중복 반환
     *
     * 재현 조건:
     *   - pageSize=1 로 커서 페이지네이션
     *   - 1페이지 마지막 선수(Player A)가 FM24+FM26 두 개의 fmPlayer 보유
     *   - 커서 조건: ca < lastCA → A의 FM24 행이 조건을 통과해 2페이지에 재등장
     *
     * 기대 동작: 2페이지에 Player A 미포함
     * 버그 동작: 2페이지에 Player A 재등장 (중복 반환)
     */
    @Test
    @DisplayName("FM24+FM26 보유 선수가 커서 페이지네이션 2페이지에 중복 반환")
    void bug2_cursor_must_not_return_duplicate_player_with_multiple_fm_versions() {
        // given
        int pageSize = 1;
        Pageable pageable = PageRequest.of(0, pageSize);

        // Player A: FM26(ca=200) + FM24(ca=150)
        // 정렬 시 FM26 행(ca=200)이 1페이지에 등장
        // 커서 조건 ca < 200 에서 FM24 행(ca=150)이 통과 → 2페이지 재등장 (Bug-2)
        Player playerA = persistMatchedPlayerWithTwoFmVersions("messi0", 1, 150, 200);

        // Player B: FM24(ca=180) — 2페이지에서 정상 등장해야 함
        persistMatchedPlayers("messi", 1, 2);
        tem.flush();
        tem.clear();

        // when — 1페이지
        Slice<Player> page1 = playerRepository.searchPlayerByName(
                "messi", pageable, null, null, null);

        assertThat(page1.getContent()).hasSize(1);
        Player lastInPage1 = page1.getContent().getFirst();
        assertThat(lastInPage1.getId())
                .as("1페이지 첫 선수는 ca=200인 Player A")
                .isEqualTo(playerA.getId());

        // when — 2페이지 (커서: playerA.id, ca=200)
        Slice<Player> page2 = playerRepository.searchPlayerByName(
                "messi", pageable,
                lastInPage1.getId(),
                lastInPage1.getFmPlayerCurrentAbility(),
                lastInPage1.getMappingStatus());

        // then
        List<Long> page2Ids = page2.getContent().stream().map(Player::getId).toList();
        assertThat(page2Ids)
                .as("Bug-2: 2페이지에 Player A가 중복 반환되면 실패")
                .doesNotContain(playerA.getId());
    }

    private void persistMatchedPlayers(String name, int startInclusive, int endExclusive) {
        for (int i = startInclusive; i < endExclusive; i++) {
            Player player = Player.builder()
                    .firstName(name + i)
                    .mappingStatus(MappingStatus.MATCHED)
                    .build();
            FmPlayer fmPlayer = FmPlayer.builder()
                    .firstName(name + i)
                    .currentAbility(100 + i * 10)
                    .fmUid(i + 1)
                    .potentialAbility(100 + i * 10)
                    .fmVersion(FmVersion.FM24)
                    .build();
            player.updateFmPlayer(fmPlayer);
            player.updateLatestFmData(fmPlayer.getCurrentAbility(), fmPlayer.getPotentialAbility(), fmPlayer.getFmVersion(), false);
            tem.persist(fmPlayer);
            tem.persist(player);
        }
    }

    private Player persistMatchedPlayerWithTwoFmVersions(
            String firstName, int fmUid, int ca24, int ca26) {
        Player player = Player.builder()
                .firstName(firstName)
                .mappingStatus(MappingStatus.MATCHED)
                .build();
        FmPlayer fm24 = FmPlayer.builder()
                .firstName(firstName).fmUid(fmUid)
                .fmVersion(FmVersion.FM24).currentAbility(ca24).potentialAbility(ca26)
                .build();
        FmPlayer fm26 = FmPlayer.builder()
                .firstName(firstName).fmUid(fmUid)
                .fmVersion(FmVersion.FM26).currentAbility(ca26).potentialAbility(ca26)
                .build();
        player.updateFmPlayer(fm24);
        player.updateFmPlayer(fm26);
        player.updateLatestFmData(ca26, ca26, fm26.getFmVersion(), false);
        tem.persist(fm24);
        tem.persist(fm26);
        tem.persist(player);
        return player;
    }
}
