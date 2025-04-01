package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.FmPlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.NationNormalizer;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitializationService {
    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final BulkRepository bulkRepository;
    private final FmPlayerRepository fmPlayerRepository;
    private final FootballApiService footballApiService;
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

    /**
     * api-football 요청 제한 -> 450/m, 7.5/s
     * 1회 요청 시 평균 0.5s 소요
     */


    public Mono<Void> saveInitialLeague() {
        List<Integer> leagueApiIds = createAllLeagueApiIds();
        return Flux.fromIterable(leagueApiIds)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .timeout(Duration.ofSeconds(TIME_OUT))
                .flatMap(leagueApiId ->
                                footballApiService.getLeagueInfo(leagueApiId)
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
                        Mono.fromRunnable(() -> leagueService.saveAllByLeagueDetails(leagues)))
                .then();
    }

    // league standing에서 team 생성
    public Mono<Void> saveInitialTeams() {
        return Mono.fromCallable(leagueRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .flatMap(league ->
                                footballApiService.getLeagueStandings(league.getLeagueApiId(), league.getCurrentSeason())
                        , 3)
                .timeout(Duration.ofSeconds(TIME_OUT))
                .doOnNext(response -> {
                    if (!response.isEmpty()) {
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
                )
                .then();
    }

    public Mono<Void> saveInitialPlayers() {
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
                        .onErrorResume(e -> {
                            log.error("리그 {}: page 1 에러 발생: {}", league.getLeagueApiId(), e.getMessage());
                            return Mono.empty();
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
                                        .onErrorResume(e -> {
                                            log.error("리그 {}: page {} 에러 발생: {}", league.getLeagueApiId(), nextPage, e.getMessage());
                                            return Mono.empty();
                                        })
                                        .retry(3); // 최대 3회 재시도
                            } else {
                                log.info("리그 {}: 모든 페이지 처리 완료", league.getLeagueApiId());
                                return Mono.empty();
                            }
                        })
                        .doOnNext(dto -> log.info("리그 {}: 페이지 {} - 플레이어 수: {}", league.getLeagueApiId(), dto.paging().current(), dto.response().size()))
                        .flatMap(dto -> {
                            List<Player> players = convertPlayerStatisticsDtoToPlayer(dto);
                            return Flux.fromIterable(players);
                        }), 2) //
                // 선수 1000명 모일시 bulk insert
                .buffer(1000)
                .concatMap(batch -> Mono.fromRunnable(() -> {
                    bulkRepository.bulkInsertPlayers(batch);
                }))
                .then();
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

    public void updateAllPlayersFmData() {
        List<Player> players = new  ArrayList<>();
        List<FmPlayer> fmPlayers = new  ArrayList<>();
        playerRepository.findByMappingStatus(PlayerMappingStatus.UNMAPPED).forEach(player -> {
            List<FmPlayer> findFmPlayer = fmPlayerRepository.findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(
                    player.getFirstName(), player.getLastName(), player.getBirth(), player.getNationName()
            );
            if (findFmPlayer.isEmpty()) {
                log.info("Not Exist FmPlayer: {}", player.getId());
            } else if (findFmPlayer.size() > 1) {
                log.info("Too Many FmPlayer: {}", player.getId());
            } else{
                log.info("Success : FmPlayer: {}", player.getId());
                FmPlayer fmPlayer = findFmPlayer.getFirst();
                player.updateFmPlayer(fmPlayer);
                player.updateMappingStatus(PlayerMappingStatus.MATCHED);
                players.add(player);
                fmPlayers.add(fmPlayer);
            }
        });
        int total = fmPlayers.size();
        for (int i = 0; i < total; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, total);
            bulkRepository.bulkUpdatePlayersFmData(players.subList(i, end), fmPlayers.subList(i, end));
        }
    }

//    public void updateAllPlayersFmDataV2(){
//        return Mono.fromCallable(playerRepository::findAll)
//                .subscribeOn(Schedulers.boundedElastic())
//                .flatMapMany(Flux::fromIterable)
//                .flatMap(player -> {
//                    Mono.fromCallable(()->fmPlayerRepository.findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(
//                            player.getFirstName(), player.getLastName(), player.getBirth(), player.getNationName()
//                    )).subscribeOn(Schedulers.boundedElastic()).flatMap(
//                            fmPlayers -> {
//                                if(fmPlayers.isEmpty()){
//                                    log.info("Not Exist FmPlayer: {}", player.getId());
//                                } else if (fmPlayers.size() > 1) {
//                                    log.info("Too Many FmPlayer: {}", player.getId());
//                                }else{
//                                    player.updateFmPlayer(fmPlayers.getFirst());
//                                }
//                                return Mono.empty();
//                            }
//                    );
//                }
//                ).then();
//    }


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


    //https://v3.football.api-sports.io/players?league=39&season=2024 한 페이지 선수 정보 리스트
    private List<Player> convertPlayerStatisticsDtoToPlayer(PlayerStatisticsApiResponseDto playerStatisticsApiResponseDto) {
        return playerStatisticsApiResponseDto.response().stream().filter(dto -> {
            return dto != null || dto.statistics() != null || dto.player() != null
                    || dto.player().birth().date() != null || dto.player().name() != null
                    || dto.player().nationality() != null;
        }).map(dto -> {
            PlayerStatisticsApiResponseDto.PlayerDto player = dto.player();
            return Player.builder()
                    .playerApiId(player.id())
                    .imageUrl(player.photo())
                    .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                    .lastName(StringUtils.getLastName(player.lastname()).toUpperCase())
                    .nationName(NationNormalizer.normalize(player.nationality().toUpperCase()))
                    .nationLogoUrl(Objects.requireNonNull(dto.statistics().getFirst().league().flag()))
                    .birth(player.birth().date())
                    .height(StringUtils.extractNumber(player.height()))
                    .weight(StringUtils.extractNumber(player.weight()))
                    .mappingStatus(PlayerMappingStatus.UNMAPPED)
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
