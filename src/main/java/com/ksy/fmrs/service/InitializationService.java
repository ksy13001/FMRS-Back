package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.service.apiClient.WebClientService;
import com.ksy.fmrs.util.StringUtils;
import com.ksy.fmrs.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitializationService {
    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final FootballApiService footballApiService;
    private final LeagueService leagueService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final WebClientService webClientService;
    private static final int LAST_LEAGUE_ID = 1172; //1172
    private static final int FIRST_LEAGUE_ID = 1;   //1
    private static final int SEASON_2024 = 2024;
    private static final int DEFAULT_PAGE = 1;
    //각 요청 사이에 약 133ms 딜레이 (450회/분 ≒ 7.5회/초)
    private static final int DELAY_MS = 150;
    private static final int TIME_OUT = 10;
    private static final int buffer = 100;

    /**
     * api-football 요청 제한 -> 450/m
     * 1회 요청 시 평균 0.5s 소요
     */


    public Mono<Void> saveInitialLeague() {
        List<Integer> leagueApiIds = createAllLeagueApiIds();
        return Flux.fromIterable(leagueApiIds)
                .buffer(buffer)
                .concatMap(batch -> Flux.fromIterable(batch)
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
                        ))
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
                )
                .timeout(Duration.ofSeconds(TIME_OUT))
                .doOnNext(response -> {
                    if (!response.isEmpty()) {
                        log.info("leagueApiId {}: 응답 있음", response.getFirst().getLeagueApiId());
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

    /**
     * 1. team 전체 조회
     * 2. teamApiId 의 squad 조회(외부 api)
     * 3. squad 순회하면서 playerApi로 저장된 player 조회
     * 4. player TeamApi 업데이트
     */
//    public Mono<Void> updateAllPlayersTeamApiId() {
//        return Mono.fromCallable(teamRepository::findAllTeamsWithLeague)
//                .subscribeOn(Schedulers.boundedElastic())
//                .flatMapMany(Flux::fromIterable)
//                .delayElements(Duration.ofMillis(DELAY_MS))
//                .doOnNext(team -> log.info("팀 처리 시작 : teamApiId={} name={}",team.getTeamApiId(), team.getName()))
//                .flatMap(team -> footballApiService.getSquadPlayers(team.getTeamApiId())
//                        .delaySubscription(Duration.ofMillis(DELAY_MS))
//                        .onErrorResume(e -> {
//                            log.error("팀 id {}: 에러 발생: {}", team.getTeamApiId(), e.getMessage());
//                            return Mono.empty();
//                        })
//                        .flatMap(dto->{
//                            team.resetSquad();
//                            dto.response().getFirst().players().stream().flatMap(
//                                    player -> {
//                                        Player findPlayer = Mono.fromCallable(()->playerRepository.findByPlayerApiId(player.id())
//                                                .orElse(null));
//                                    }
//                            )
//                        })
//    }

    // 각 리그의 시즌 으로 할 경우 시즌 시작하지 않은 리그는 선수가 조회 안되기 때문에 일단 2024 시즌으로 고정
    public Mono<Void> saveInitialPlayers() {
        return Mono.fromCallable(leagueRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(leagues -> log.info("조회된 리그 개수: {}", leagues.size()))
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
                        .retry(3) // 최대 3회 재시도
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
                            try {
                                List<Player> players = convertPlayerStatisticsDtoToPlayer(dto);
                                log.info("리그 {}: 총 플레이어 수: {}", league.getLeagueApiId(), players.size());
                                return Flux.fromIterable(players);
                            } catch (Exception ex) {
                                log.error("리그 {}: DTO -> Player 변환 중 에러 발생: {}", league.getLeagueApiId(), ex.getMessage());
                                return Flux.empty();
                            }
                        }), 3) //
                // 선수 1000명 모일시 saveAll
                .collectList()
                .flatMap(players -> {
                    return Mono.fromRunnable(()->playerService.saveAll(players));
                }).then();
//                .buffer(100).concatMap(batch -> Flux.fromIterable(batch)
//                        .flatMap(players -> {
//                            log.info("배치 처리 중 - 플레이어 수: {}", batch.size());
//                            return Mono.fromRunnable(() -> playerService.saveAll(batch));
//                        }))
//                .then();
    }

    @Transactional
    public void updatePlayerFmStat(List<FmPlayerDto> fmPlayers) {
        fmPlayers.forEach(fmPlayer -> {
            String name = StringUtils.getPlayerNameFromFileName(fmPlayer.getName());
            List<Player> findPlayer = playerRepository.searchPlayerByFm(
                    StringUtils.getFirstName(name).toUpperCase(),
                    StringUtils.getLastName(name).toUpperCase(),
                    fmPlayer.getBorn(),
                    fmPlayer.getNation().getName().toUpperCase()
            );
            if (findPlayer == null || findPlayer.isEmpty()) {
                return;
            }
            findPlayer.getFirst().updateFmData(
                    getPersonalityAttributesFromFmPlayer(fmPlayer),
                    getTechnicalAttributesFromFmPlayer(fmPlayer),
                    getMentalAttributesFromFmPlayer(fmPlayer),
                    getPhysicalAttributesFromFmPlayer(fmPlayer),
                    getGoalKeeperAttributesFromFmPlayer(fmPlayer),
                    getHiddenAttributesFromFmPlayer(fmPlayer),
                    fmPlayer.getCurrentAbility(),
                    fmPlayer.getPotentialAbility());
        });
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

    private void saveInitialLeagues(List<LeagueDetailsRequestDto> leagueDetailsRequestDto) {
        leagueService.saveAllByLeagueDetails(leagueDetailsRequestDto);
    }

//    public void savePlayersFromFmPlayers(String dirPath) {
//        playerRepository.saveAll(getPlayersFromFmPlayers(dirPath));
//    }

    public List<FmPlayerDto> getPlayersFromFmPlayers(String dirPath) {
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        return Arrays.stream(Objects.requireNonNull(jsonFiles))
                .map(file -> {
                    try {
                        // JSON 파일을 FmPlayerDto로 변환
                        FmPlayerDto dto = objectMapper.readValue(file, FmPlayerDto.class);
                        dto.setName(file.getName().toUpperCase());
                        return dto;
                    } catch (Exception e) {
                        // 변환에 실패하면 에러 로그 남기고 null 반환 (또는 예외 전파)
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

//    private Player importPlayerFromJson(File file) throws IOException {
//        FmPlayerDto fmPlayerDto = objectMapper.readValue(file, FmPlayerDto.class);
//        return convertFmPlayerDtoToPlayer(StringUtils.getPlayerNameFromFileName(file.getName()), fmPlayerDto);
//    }

    //https://v3.football.api-sports.io/players?league=39&season=2024 한 페이지 선수 정보 리스트
    private List<Player> convertPlayerStatisticsDtoToPlayer(PlayerStatisticsApiResponseDto playerStatisticsApiResponseDto) {
        return playerStatisticsApiResponseDto.response().stream().filter(dto -> {
            return dto != null || dto.statistics() != null || dto.player() != null;
        }).map(dto -> {
            PlayerStatisticsApiResponseDto.PlayerDto player = dto.player();
            return Player.builder()
                    .name(player.name())
                    .playerApiId(player.id())
                    .teamApiId(Objects.requireNonNull(dto.statistics()).getFirst().team().id())
                    .leagueApiId(Objects.requireNonNull(dto.statistics()).getFirst().league().id())
                    .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                    .lastName(StringUtils.getLastName(player.lastname()).toUpperCase())
                    .nationName(player.nationality().toUpperCase())
                    .nationLogoUrl(Objects.requireNonNull(dto.statistics().getFirst().league().flag()))
                    .age(player.age())
                    .birth(player.birth().date())
                    .height(StringUtils.extractNumber(player.height()))
                    .weight(StringUtils.extractNumber(player.weight()))
                    .build();
        }).toList();
    }

    private Player convertFmPlayerDtoToPlayer(String name, FmPlayerDto fmPlayerDto) {
        return Player.builder()
                .name(name)
                .firstName(StringUtils.getFirstName(name))
                .lastName(StringUtils.getLastName(name))
                .age(TimeUtils.getAge(fmPlayerDto.getBorn()))
                .birth(fmPlayerDto.getBorn())
                .height(fmPlayerDto.getHeight())
                .weight(fmPlayerDto.getWeight())
                .currentAbility(fmPlayerDto.getCurrentAbility())
                .potentialAbility(fmPlayerDto.getPotentialAbility())
                .goalKeeperAttributes(getGoalKeeperAttributesFromFmPlayer(fmPlayerDto))
                .hiddenAttributes(getHiddenAttributesFromFmPlayer(fmPlayerDto))
                .mentalAttributes(getMentalAttributesFromFmPlayer(fmPlayerDto))
                .personalityAttributes(getPersonalityAttributesFromFmPlayer(fmPlayerDto))
                .physicalAttributes(getPhysicalAttributesFromFmPlayer(fmPlayerDto))
                .technicalAttributes(getTechnicalAttributesFromFmPlayer(fmPlayerDto))
                .position(getPositionFromFmPlayer(fmPlayerDto))
                .build();
    }

    private GoalKeeperAttributes getGoalKeeperAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.GoalKeeperAttributesDto goalKeeperAttributesDto = fmPlayerDto.getGoalKeeperAttributes();
        if (goalKeeperAttributesDto == null) {
            return null;
        }
        return GoalKeeperAttributes.builder()
                .aerialAbility(goalKeeperAttributesDto.getAerialAbility())
                .commandOfArea(goalKeeperAttributesDto.getCommandOfArea())
                .communication(goalKeeperAttributesDto.getCommunication())
                .eccentricity(goalKeeperAttributesDto.getEccentricity())
                .handling(goalKeeperAttributesDto.getHandling())
                .kicking(goalKeeperAttributesDto.getKicking())
                .oneOnOnes(goalKeeperAttributesDto.getOneOnOnes())
                .reflexes(goalKeeperAttributesDto.getReflexes())
                .rushingOut(goalKeeperAttributesDto.getRushingOut())
                .tendencyToPunch(goalKeeperAttributesDto.getTendencyToPunch())
                .throwing(goalKeeperAttributesDto.getThrowing())
                .build();
    }

    private HiddenAttributes getHiddenAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.HiddenAttributesDto hiddenAttributesDto = fmPlayerDto.getHiddenAttributes();
        if (hiddenAttributesDto == null) {
            return null;
        }
        return HiddenAttributes.builder()
                .consistency(hiddenAttributesDto.getConsistency())
                .dirtiness(hiddenAttributesDto.getDirtiness())
                .importantMatches(hiddenAttributesDto.getImportantMatches())
                .injuryProneness(hiddenAttributesDto.getInjuryProness())
                .versatility(hiddenAttributesDto.getVersatility())
                .build();
    }

    private MentalAttributes getMentalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.MentalAttributesDto mentalAttributesDto = fmPlayerDto.getMentalAttributes();
        if (mentalAttributesDto == null) {
            return null;
        }
        return MentalAttributes.builder()
                .aggression(mentalAttributesDto.getAggression())
                .anticipation(mentalAttributesDto.getAnticipation())
                .bravery(mentalAttributesDto.getBravery())
                .composure(mentalAttributesDto.getComposure())
                .concentration(mentalAttributesDto.getConcentration())
                .decisions(mentalAttributesDto.getDecisions())
                .determination(mentalAttributesDto.getDetermination())
                .flair(mentalAttributesDto.getFlair())
                .leadership(mentalAttributesDto.getLeadership())
                .build();
    }

    private PersonalityAttributes getPersonalityAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PersonalityAttributesDto personalityAttributesDto = fmPlayerDto.getPersonalityAttributes();
        if (personalityAttributesDto == null) {
            return null;
        }
        return PersonalityAttributes.builder()
                .adaptability(personalityAttributesDto.getAdaptability())
                .ambition(personalityAttributesDto.getAmbition())
                .loyalty(personalityAttributesDto.getLoyalty())
                .pressure(personalityAttributesDto.getPressure())
                .professional(personalityAttributesDto.getProfessional())
                .sportsmanship(personalityAttributesDto.getSportsmanship())
                .temperament(personalityAttributesDto.getTemperament())
                .controversy(personalityAttributesDto.getControversy())
                .build();
    }

    private PhysicalAttributes getPhysicalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PhysicalAttributesDto physicalAttributesDto = fmPlayerDto.getPhysicalAttributes();
        if (physicalAttributesDto == null) {
            return null;
        }
        return PhysicalAttributes.builder()
                .acceleration(physicalAttributesDto.getAcceleration())
                .agility(physicalAttributesDto.getAgility())
                .balance(physicalAttributesDto.getBalance())
                .jumpingReach(physicalAttributesDto.getJumping())
                .naturalFitness(physicalAttributesDto.getNaturalFitness())
                .pace(physicalAttributesDto.getPace())
                .stamina(physicalAttributesDto.getStamina())
                .strength(physicalAttributesDto.getStrength())
                .build();
    }

    private TechnicalAttributes getTechnicalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.TechnicalAttributesDto technicalAttributesDto = fmPlayerDto.getTechnicalAttributes();
        if (technicalAttributesDto == null) {
            return null;
        }
        return TechnicalAttributes.builder()
                .corners(technicalAttributesDto.getCorners())
                .crossing(technicalAttributesDto.getCrossing())
                .dribbling(technicalAttributesDto.getDribbling())
                .finishing(technicalAttributesDto.getFinishing())
                .firstTouch(technicalAttributesDto.getFirstTouch())
                .freeKincks(technicalAttributesDto.getFreekicks())
                .heading(technicalAttributesDto.getHeading())
                .longShots(technicalAttributesDto.getLongShots())
                .longThrows(technicalAttributesDto.getLongthrows())
                .marking(technicalAttributesDto.getMarking())
                .passing(technicalAttributesDto.getPassing())
                .penaltyTaking(technicalAttributesDto.getPenaltyTaking())
                .tackling(technicalAttributesDto.getTackling())
                .technique(technicalAttributesDto.getTechnique())
                .build();
    }

    private Position getPositionFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PositionAttributesDto positionAttributesDto = fmPlayerDto.getPositions();
        if (positionAttributesDto == null) {
            return null;
        }
        return Position.builder()
                .goalkeeper(positionAttributesDto.getGoalkeeper())
                .defenderCentral(positionAttributesDto.getDefenderCentral())
                .defenderLeft(positionAttributesDto.getDefenderLeft())
                .defenderRight(positionAttributesDto.getDefenderRight())
                .wingBackLeft(positionAttributesDto.getWingBackLeft())
                .wingBackRight(positionAttributesDto.getWingBackRight())
                .defensiveMidfielder(positionAttributesDto.getDefensiveMidfielder())
                .midfielderCentral(positionAttributesDto.getMidfielderCentral())
                .midfielderLeft(positionAttributesDto.getMidfielderLeft())
                .midfielderRight(positionAttributesDto.getMidfielderRight())
                .attackingMidCentral(positionAttributesDto.getAttackingMidCentral())
                .attackingMidLeft(positionAttributesDto.getAttackingMidLeft())
                .attackingMidRight(positionAttributesDto.getAttackingMidRight())
                .striker(positionAttributesDto.getStriker())
                .build();
    }
}
