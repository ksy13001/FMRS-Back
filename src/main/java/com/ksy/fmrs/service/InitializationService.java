package com.ksy.fmrs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.FmPlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerRawRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.NationNormalizer;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitializationService {
    private final ObjectMapper objectMapper;
    private final LeagueRepository leagueRepository;
    private final BulkRepository bulkRepository;
    private final FootballApiService footballApiService;
    private final PlayerRawRepository playerRawRepository;
    private final LeagueService leagueService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private static final int LAST_LEAGUE_ID = 1172; //1172
    private static final int FIRST_LEAGUE_ID = 1;   //1
    private static final int SEASON_2024 = 2024;
    private static final int DEFAULT_PAGE = 1;
    //각 요청 사이에 약 133ms 딜레이 (450회/분 ≒ 7.5회/초)
    private static final int DELAY_MS = 150;
    private static final int TIME_OUT = 10;
    private static final int CHUNK_SIZE = 1000;
    private final LocaleResolver localeResolver;

    /**
     * api-football 요청 제한 -> 450/m, 7.5/s
     * 1회 요청 시 평균 0.5s 소요
     */


    public Mono<Void> saveInitialLeague() {
        List<Integer> leagueApiIds = createAllLeagueApiIds();
        return Flux.fromIterable(leagueApiIds)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .flatMap(leagueApiId ->
                                footballApiService.getLeagueInfo(leagueApiId)
                                        .timeout(Duration.ofSeconds(TIME_OUT))
                                        .publishOn(Schedulers.boundedElastic())
                                        .doOnNext(response -> {
                                            if (response.isPresent()) {
                                                log.info("leagueApiId {}: 응답 있음", leagueApiId);
                                            } else {
                                                log.info("leagueApiId {}: 응답 없음", leagueApiId);
                                            }
                                        })
                                        .onErrorResume(e -> {
                                            log.error("leagueApiId {} 에러 발생: {}", leagueApiId, e.getMessage());
                                            return Mono.empty();
                                        })
                        , 3)
                .filter(response -> response.isPresent() && isLeagueType(response.get()))
                .map(Optional::get)
                .collectList()
                .doOnNext(leagues -> log.info("최종 저장할 리그 개수: {}", leagues.size()))
                .flatMap(leagues ->
                        Mono.fromRunnable(() -> leagueService.saveAllByLeagueDetails(leagues))
                                .subscribeOn(Schedulers.boundedElastic())
                .then());
    }


    // league standing에서 team 생성
    public Mono<Void> saveInitialTeams() {
        return Mono.fromCallable(leagueRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .flatMap(league ->
                                footballApiService.getLeagueStandings(league.getLeagueApiId(), league.getCurrentSeason())
                                        .timeout(Duration.ofSeconds(TIME_OUT))
                        , 3)
                .doOnNext(response -> {
                    if (response.isEmpty()) {
                        log.info("leagueApiId {}: 응답 없음", response.getFirst().getLeagueApiId());
                    } else {
                        log.info("leagueApiId {}: 응답 있음", response.getFirst().getLeagueApiId());
                    }
                })
                .onErrorResume(e -> {
                    log.error("leagueApiId {} 에러 발생: {}", e.getMessage());
                    return Mono.empty();
                })
                .flatMap(Flux::fromIterable)
                .collectList()
                .flatMap(teamStandingDtos ->
                        Mono.fromRunnable(() -> teamService.saveAllByTeamStanding(teamStandingDtos))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .then();
    }

    public Mono<Void> saveInitialPlayers() {
        AtomicInteger cnt = new AtomicInteger(0);
        return Mono.fromCallable(leagueRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .delayElements(Duration.ofMillis(DELAY_MS)) // 요청 간 150ms 간격 (분당 400회 요청)
                .doOnNext(league -> log.info("리그 처리 시작 - leagueApiId={}, leagueName={}", league.getLeagueApiId(), league.getName()))
                .flatMap(league -> footballApiService.getPlayerStatisticsByLeagueId(
                                league.getLeagueApiId(),
                                league.getCurrentSeason(),
                                DEFAULT_PAGE)
                        .delaySubscription(Duration.ofMillis(DELAY_MS)) // 요청 간 150ms 간격
                        .timeout(Duration.ofSeconds(30)) // 타임아웃 30초
                        .onErrorContinue((e, o) -> {
                            log.error("리그 {}: page 1 에러 발생: {}", league.getLeagueApiId(), e.getMessage());
                        })
                        .expand(dto -> {
                            int current = dto.paging().current();
                            int total = dto.paging().total();
                            if (current < total) {
                                int nextPage = current + 1;
                                return footballApiService.getPlayerStatisticsByLeagueId(
                                                league.getLeagueApiId(),
                                                league.getCurrentSeason(),
                                                nextPage)
                                        .delaySubscription(Duration.ofMillis(DELAY_MS)) // 요청 간 150ms 간격
                                        .timeout(Duration.ofSeconds(30)) // 타임아웃 30초
                                        .onErrorContinue((e, ex) -> {
                                            log.error("리그 {}: page {} 에러 발생: {}", league.getLeagueApiId(), nextPage, e.getMessage());
                                        });
                            } else {
                                log.info("리그 {}: 모든 페이지 처리 완료", league.getLeagueApiId());
                                return Mono.empty();
                            }
                        })
                        .doOnNext(dto -> {
                            log.info("리그 {}: 페이지 {} - 플레이어 수: {}", league.getLeagueApiId(), dto.paging().current(), dto.response().size());
                            int total = cnt.addAndGet(dto.response().size());
                            log.info("현재 받아온 총 플레이어 수: {}", total);
                        })
                        .flatMap(dto -> {
                            List<Player> players = convertPlayerStatisticsDtoToPlayer(dto);
                            return Flux.fromIterable(players);
                        }), 3) //
                // 선수 1000명 모일시 bulk insert
                .buffer(1000)
                .concatMap(batch -> Mono.fromRunnable(() -> bulkRepository.bulkInsertPlayers(batch)))
                .onErrorContinue((e, o) -> {
                    log.info("저장 중 애러 발생 : {}", e.getMessage());
                }).then();
    }


    public void saveFmPlayers(String dirPath) {
        List<FmPlayerDto> fmPlayerDtos = getPlayersFromFmPlayers(dirPath);
        log.info("fmPlayer 저장 시작: {}", fmPlayerDtos.size());
        List<FmPlayer> fmPlayers = new ArrayList<>();
        fmPlayerDtos.forEach(fmPlayer -> {
            fmPlayers.add(FmPlayer.FmPlayerDtoToEntity(fmPlayer));
        });
        int total = fmPlayers.size();
        // 1000개씩 bulk insert
        for (int i = 0; i < total; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, total);
            List<FmPlayer> now = fmPlayers.subList(i, end);
            bulkRepository.bulkInsertFmPlayers(now);
        }
    }

    public Mono<Void> savePlayerRaws() {
        AtomicInteger cnt = new AtomicInteger(0);
        return Mono.fromCallable(leagueRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .flatMap(league -> {
                    return footballApiService.getPlayerStatisticsToStringByLeagueId(
                                    league.getLeagueApiId(), league.getCurrentSeason(), DEFAULT_PAGE)
                            .delaySubscription(Duration.ofMillis(DELAY_MS))
                            .timeout(Duration.ofSeconds(60))
                            .onErrorContinue((e, o) -> log.info("league 페이지 애러: {}", league.getLeagueApiId()))
                            .doOnNext(json -> log.info("리그 처리 시작:{}, 페이지:{}", league.getLeagueApiId(), DEFAULT_PAGE))
                            .expand(response -> {
                                LeagueApiPlayersDto dto = null;
                                try {
                                    dto = objectMapper
                                            .readValue(response, LeagueApiPlayersDto.class);
                                    cnt.addAndGet(dto.response().size());
                                    log.info("리그 처리 시작:{}, 페이지:{}, 선수 수:{}, 현재 페이지 수:{}",
                                            league.getLeagueApiId(), dto.paging().current(), dto.response().size(), cnt.get());
                                } catch (JsonProcessingException e) {
                                    log.info("리그 to dto 애러- id:{}, name:{}", league.getLeagueApiId(), league.getName());
                                }
                                int total = dto.paging().total();
                                int current = dto.paging().current();
                                if (current < total) {
                                    int nextPage = current + 1;
                                    return footballApiService.getPlayerStatisticsToStringByLeagueId(
                                                    league.getLeagueApiId(), league.getCurrentSeason(), nextPage)
                                            .delaySubscription(Duration.ofMillis(DELAY_MS))
                                            .timeout(Duration.ofSeconds(60))
                                            .onErrorContinue((e, ex) -> {
                                                log.info(e.getMessage(), e);
                                            });
                                } else {
                                    log.info("리그 페이지 끝 : {}", league.getLeagueApiId());
                                    return Mono.empty();
                                }
                            });
                }, 3)
                .filter(Objects::nonNull)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .buffer(CHUNK_SIZE)
                .flatMap(chunk ->
                                Mono.fromRunnable(() -> bulkRepository.bulkInsertPlayerRaws(chunk))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .doOnError(e -> log.error("bulk insert 실패, size={}", chunk.size(), e))
                        , 3)
                .then();
    }

    public void initializePlayerFromPlayerRaw(){
        playerRawRepository.findAll().forEach(playerRaw -> {
            try {
                playerService.savePlayersByPlayerRaw(playerRaw);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Transactional
    public int updateAllPlayersFmData() {
//        List<Player> players = new ArrayList<>();
//        List<FmPlayer> fmPlayers = new ArrayList<>();
//        playerRepository.findByMappingStatus(PlayerMappingStatus.UNMAPPED).forEach(player -> {
//            List<FmPlayer> findFmPlayer = fmPlayerRepository.findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(
//                    player.getFirstName(), player.getLastName(), player.getBirth(), player.getNationName()
//            );
//            if(findFmPlayer.size() == 1){
//                log.info("Add fmPlayer : id{}", findFmPlayer.getFirst().getId());
//                fmPlayers.add(findFmPlayer.getFirst());
//            }
//        });
//        int total = fmPlayers.size();
//        for (int i = 0; i < total; i += CHUNK_SIZE) {
//            int end = Math.min(i + CHUNK_SIZE, total);
//            bulkRepository.bulkUpdatePlayersFmData(players.subList(i, end), fmPlayers.subList(i, end));
//        }
        return bulkRepository.mappingPlayerAndFmPlayer();
    }

    @Transactional
    public int updatePlayersAsFailedByDuplicatedFmPlayer(){
        return bulkRepository.updatePlayersAsFailedByDuplicatedFmPlayer();
    }

    private List<Integer> createAllLeagueApiIds() {
        List<Integer> urls = new ArrayList<>();
        for (int i = FIRST_LEAGUE_ID; i <= LAST_LEAGUE_ID; i++) {
            urls.add(i);
        }
        return urls;
    }

    private Boolean isLeagueType(LeagueDetailsRequestDto leagueDetailsRequestDto) {
        return leagueDetailsRequestDto != null &&
                leagueDetailsRequestDto.getLeagueType().equals(LeagueType.LEAGUE.getValue()) &&
                leagueDetailsRequestDto.getCurrentSeason() >= SEASON_2024 &&
                leagueDetailsRequestDto.getStanding();
    }

    private List<FmPlayerDto> getPlayersFromFmPlayers(String dirPath) {
        log.info("dir 탐색 시작");
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        return Arrays.stream(Objects.requireNonNull(jsonFiles))
                .map(file -> {
                    try {
                        // JSON 파일을 FmPlayerDto로 변환
                        FmPlayerDto dto = objectMapper.readValue(file, FmPlayerDto.class);
                        dto.setName(StringUtils.getPlayerNameFromFileName(file.getName().toUpperCase()));
                        return dto;
                    } catch (Exception e) {
                        // 변환에 실패하면 에러 로그 남기고 null 반환 (또는 예외 전파)
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private List<PlayerRaw> convertPlayerStatisticsDtoToPlayerRaws(List<String> playerStatisticsApiResponseDtos) {
        return playerStatisticsApiResponseDtos.stream().map(dto -> {
            return PlayerRaw.builder()
                    .jsonRaw(dto)
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();
        }).toList();
    }

    //https://v3.football.api-sports.io/players?league=39&season=2024 한 페이지 선수 정보 리스트
    private List<Player> convertPlayerStatisticsDtoToPlayer(LeagueApiPlayersDto leagueApiPlayersDto) {
        return leagueApiPlayersDto.response().stream().filter(Objects::nonNull).map(dto -> {
            LeagueApiPlayersDto.PlayerDto player = dto.player();
            return Player.builder()
                    .playerApiId(player.id())
                    .imageUrl(player.photo())
                    .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                    .lastName(StringUtils.getLastName(player.name()).toUpperCase())
                    .nationName(NationNormalizer.normalize(player.nationality().toUpperCase()))
                    .nationLogoUrl(Objects.requireNonNull(dto.statistics().getFirst().league().flag()))
                    .birth(player.birth().date())
                    .height(StringUtils.extractNumber(player.height()))
                    .weight(StringUtils.extractNumber(player.weight()))
                    .mappingStatus(MappingStatus.UNMAPPED)
                    .build();
        }).toList();
    }

//    private Player convertFmPlayerDtoToPlayer(String name, FmPlayerDto fmPlayerDto) {
//        return Player.builder()
//                .name(name)
//                .firstName(StringUtils.getFirstName(name))
//                .lastName(StringUtils.getLastName(name))
//                .birth(fmPlayerDto.getBorn())
//                .height(fmPlayerDto.getHeight())
//                .weight(fmPlayerDto.getWeight())
//                .currentAbility(fmPlayerDto.getCurrentAbility())
//                .potentialAbility(fmPlayerDto.getPotentialAbility())
//                .goalKeeperAttributes(FmUtils.getGoalKeeperAttributesFromFmPlayer(fmPlayerDto))
//                .hiddenAttributes(FmUtils.getHiddenAttributesFromFmPlayer(fmPlayerDto))
//                .mentalAttributes(FmUtils.getMentalAttributesFromFmPlayer(fmPlayerDto))
//                .personalityAttributes(FmUtils.getPersonalityAttributesFromFmPlayer(fmPlayerDto))
//                .physicalAttributes(FmUtils.getPhysicalAttributesFromFmPlayer(fmPlayerDto))
//                .technicalAttributes(FmUtils.getTechnicalAttributesFromFmPlayer(fmPlayerDto))
//                .position(FmUtils.getPositionFromFmPlayer(fmPlayerDto))
//                .build();
//    }
}
