package com.ksy.fmrs.scheduler;


import com.ksy.fmrs.dto.apiFootball.SquadApiResponseDto;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.ReactiveUpdateService;
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

    private final ReactiveUpdateService reactiveUpdateService;
    private final TeamRepository teamRepository;

    @Scheduled(cron = "${init.update_squad_time}", zone = "Asia/Seoul")
    public void updateAllSquad() {
        reactiveUpdateService.upsertSquadMember(
                teamRepository.findAll()
        ).subscribe();
    }
}
