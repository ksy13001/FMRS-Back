package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.player.*;
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
import reactor.util.retry.Retry;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

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
    private static final int DELAY_MS = 133;
    private static final int TIME_OUT = 10;
    private static final int buffer = 100;

    /**
     * 1. 외부 api로 전체 리그 정보 다 가져옴
     * 2. 외부 api로 리그마다 standing 가져와서 팀 정보 다 가져옴
     * 3. 외부 api로 팀 마다 statisitcs 가져와서 선수 정보 다 가져옴(player_api_id 매핑하기 위함)
     * 4. bulk insert
     **/
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
                        Mono.fromRunnable(() -> saveInitialLeagues(leagues)))
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

    public Mono<Void> updateAllPlayerApiIds() {
        return Mono.fromCallable(teamRepository::findAllTeamsWithLeague)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .delayElements(Duration.ofMillis(DELAY_MS))
                .flatMap(team -> {
                    return footballApiService.getSquadStatistics(
                                    team.getTeamApiId(),
                                    team.getLeague().getLeagueApiId(),
                                    SEASON_2024,
                                    1)  // 첫 페이지 호출
                            .retryWhen(
                                    Retry.backoff(3, Duration.ofSeconds(1))
                                            .doBeforeRetry(rs -> log.warn("팀 {}: 첫 페이지 재시도, 시도 횟수: {}",
                                                    team.getTeamApiId(), rs.totalRetriesInARow() + 1))
                            )
                            .doOnNext(firstPage -> {
                                if (firstPage != null) {
                                    log.info("팀 {}: 첫 페이지 응답 받음, 현재 페이지: {} / 총 페이지: {}",
                                            team.getTeamApiId(), firstPage.paging().current(), firstPage.paging().total());
                                } else {
                                    log.warn("팀 {}: 첫 페이지 응답이 null", team.getTeamApiId());
                                }
                            })
                            .expand(response -> {
                                int currentPage = response.paging().current();
                                int totalPages = response.paging().total();
                                if (currentPage < totalPages) {
                                    int nextPage = currentPage + 1;
                                    return footballApiService.getSquadStatistics(
                                                    team.getTeamApiId(),
                                                    team.getLeague().getLeagueApiId(),
                                                    SEASON_2024,
                                                    nextPage)
                                            .retryWhen(
                                                    Retry.backoff(3, Duration.ofSeconds(1))
                                                            .doBeforeRetry(rs -> log.warn("팀 {}: 페이지 {} 재시도, 시도 횟수: {}",
                                                                    team.getTeamApiId(), nextPage, rs.totalRetriesInARow() + 1))
                                            );
                                } else {
                                    log.debug("팀 {}: 마지막 페이지 도달 (현재 페이지: {})", team.getTeamApiId(), currentPage);
                                    return Mono.empty();
                                }
                            })
                            .flatMap(dto -> {
                                if (dto == null) {
                                    log.warn("팀 {}: 응답 dto가 null", team.getTeamApiId());
                                    return Flux.empty();
                                }
                                log.debug("팀 {}: dto.response() 크기: {}", team.getTeamApiId(), dto.response().size());
                                return Flux.fromIterable(dto.response());
                            })
                            .doOnNext(playerWrapper -> {
                                log.debug("팀 {}: 선수 {} 업데이트 호출",
                                        team.getTeamApiId(), playerWrapper.player().name());
                                try {
                                    playerService.updatePlayerApiIdByPlayerWrapperDto(
                                            playerWrapper.player().id(),
                                            playerWrapper.player().firstname(),
                                            playerWrapper.player().lastname(),
                                            StringUtils.parseStringToLocalDate(playerWrapper.player().birth().date()));
                                    log.debug("팀 {}: 선수 {} 업데이트 성공", team.getTeamApiId(), playerWrapper.player().name());
                                } catch (Exception ex) {
                                    log.error("팀 {}: 선수 {} 업데이트 중 예외 발생: {}",
                                            team.getTeamApiId(), playerWrapper.player().name(), ex.getMessage());
                                }
                            })
                            .onErrorContinue((e, o) -> {
                                log.error("팀 {} 처리 중 에러 발생: {}. 문제 데이터: {}", team.getTeamApiId(), e.getMessage(), o);
                            });
                }, 10)
                .timeout(Duration.ofSeconds(TIME_OUT))
                .onErrorResume(e -> {
                    log.error("전체 처리 중 타임아웃 또는 에러 발생: {}", e.getMessage());
                    return Mono.empty();
                })
                .then();
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

    public void savePlayersFromFmPlayers(String dirPath) {
        playerRepository.saveAll(getPlayersFromFmPlayers(dirPath));
    }

    public List<Player> getPlayersFromFmPlayers(String dirPath) {
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        List<Player> players = new ArrayList<>();
        Arrays.stream(jsonFiles).forEach(file -> {
            try {
                players.add(importPlayerFromJson(file));
            } catch (Exception e) {
                log.error("Error importing file {}: {}", file.getName(), e.getMessage(), e);
            }
        });
        return players;
    }

    private Player importPlayerFromJson(File file) throws IOException {
        FmPlayerDto fmPlayerDto = objectMapper.readValue(file, FmPlayerDto.class);
        return convertFmPlayerDtoToPlayer(StringUtils.getPlayerNameFromFileName(file.getName()), fmPlayerDto);
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
                .marketValue(fmPlayerDto.getAskingPrice())
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
