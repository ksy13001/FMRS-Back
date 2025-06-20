package com.ksy.fmrs.scheduler;


import com.ksy.fmrs.dto.apiFootball.SquadApiResponseDto;
import com.ksy.fmrs.repository.global.BulkRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.service.global.FootballApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerUpdateScheduler {

    private final TeamRepository teamRepository;
    private final BulkRepository bulkRepository;
    private final FootballApiService footballApiService;

    private static final int DELAY_MS = 150;
    private static final int TIME_OUT = 10;

    @Scheduled(cron = "${init.update_squad_time}", zone = "Asia/Seoul")
    public void updateAllSquad() {
        Mono.fromCallable(teamRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(team -> {
                    log.info("Team ID: {}", team.getId());
                    return footballApiService.getSquadPlayers(team.getTeamApiId())
                            .delayElement(Duration.ofMillis(DELAY_MS))
                            .timeout(Duration.ofSeconds(TIME_OUT))
                            .filter(squadApiResponseDto -> !squadApiResponseDto.response().isEmpty())
                            .map(squadApiResponseDto -> squadApiResponseDto.response().getFirst().players()
                            )
                            .map(playerDtos -> playerDtos.stream().map(SquadApiResponseDto.Player::id)
                                    .collect(Collectors.toList()))
                            .flatMap(players -> {
                                return Mono.fromRunnable(()->bulkRepository.updatePlayersTeam(players, team.getId()))
                                        .subscribeOn(Schedulers.boundedElastic());
                            })
                            .doOnNext(t->log.info("팀 스쿼드 업데이트 : {}"+team.getId()));
                }, 3)
                .onErrorContinue((e, o) -> log.error("팀 {} 처리 실패", o, e))
                .then()
                .subscribe();
    }
}
