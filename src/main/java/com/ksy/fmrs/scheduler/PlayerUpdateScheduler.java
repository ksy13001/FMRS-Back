package com.ksy.fmrs.scheduler;


import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.service.ReactiveUpdateService;
import com.ksy.fmrs.service.SportsDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerUpdateScheduler {

    private final ReactiveUpdateService reactiveUpdateService;
    private final TeamRepository teamRepository;
    private final SportsDataSyncService sportsDataSyncService;

    @Scheduled(cron = "${init.update_squad_time}", zone = "Asia/Seoul")
    public void updateAllSquad() {
        sportsDataSyncService.syncSquadMembers(
                teamRepository.findTeamsByLeagueType(LeagueType.LEAGUE)
        );
    }

}
