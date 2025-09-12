package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import com.ksy.fmrs.repository.BulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactiveUpdateService {

    private final BulkRepository bulkRepository;
    private final FootballApiService footballApiService;

    private static final int DELAY_MS = 150;
    private static final int TIME_OUT = 10;

    public Mono<Void> upsertSquadMember(List<Team> teams) {
        return Flux.fromIterable(teams)
                .flatMap(team -> {
                    log.info("Team squad update ID: {}", team.getId());
                    return footballApiService.getSquadPlayers(team.getTeamApiId())
                            .delayElement(Duration.ofMillis(DELAY_MS))
                            .timeout(Duration.ofSeconds(TIME_OUT))
                            .filter(squadApiResponseDto -> !squadApiResponseDto.response().isEmpty())
                            .map(squadApiResponseDto -> squadApiResponseDto.response().getFirst().players()
                            )
                            .map(playerDtos -> playerDtos.stream().map(ApiFootballSquad.Player::id)
                                    .collect(Collectors.toList()))
                            .flatMap(players -> {
                                return Mono.fromRunnable(() -> bulkRepository.updatePlayersTeam(players, team.getId()))
                                        .subscribeOn(Schedulers.boundedElastic());
                            })
                            .doOnNext(t -> log.info("팀 스쿼드 업데이트 : {}" + team.getId()));
                }, 3)
                .onErrorContinue((e, o) -> log.error("팀 {} 처리 실패", o, e))
                .then();
    }
}
