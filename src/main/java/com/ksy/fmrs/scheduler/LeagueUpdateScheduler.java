package com.ksy.fmrs.scheduler;

import com.ksy.fmrs.service.LeagueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeagueUpdateScheduler {
    private final LeagueService leagueService;

    @Scheduled(cron = "${init.update_league_time}", zone = "Asia/Seoul")
    public void updateLeagueSeason(){
        log.info("start update league season");
        leagueService.findLeaguesApiIdsOutsideSeason()
                .forEach(apiIds->{
                    try {
                        leagueService.findLeagueApiInfo(apiIds)
                                .ifPresent(dto->
                                        leagueService.refreshLeagueSeason(apiIds, dto));
                    } catch (Exception e) {
                       log.info("fail to update league season : leagueApiId ={}", apiIds);
                    }
                });
    }
}
