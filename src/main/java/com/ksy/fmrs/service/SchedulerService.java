package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.LeagueStandingDto;
import com.ksy.fmrs.dto.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.TeamDetailsDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SchedulerService {
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final FootballApiService footballApiService;
    private final LeagueService leagueService;
    private final TeamService teamService;
    private static final int Last_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;
    private static final int SEASON_2024 = 2024;


    /**
     * 0. 리그 탐색 전 리그 정보에서 type 확인해야함 - type = League 인 경우만 저장
     * 1. Api-football 에 등록된 league id 불러와서 db에 저장
     * 2. 각 리그에 team 정보 불러와서 db에 저장
     * 3. 각 리그에 squad 요청으로 선수 - 팀 업데이트 (선수 데이터 미리 등록 필요함)
     * **/
    public void createInitialLeague() {
        for(int nowLeagueApiId = FIRST_LEAGUE_ID; nowLeagueApiId <= Last_LEAGUE_ID; nowLeagueApiId++){
            if(leagueRepository.findLeagueByLeagueApiId(nowLeagueApiId).isPresent()){
                continue;
            }
            LeagueDetailsRequestDto leagueDetailsRequestDto = footballApiService.getLeagueInfo(nowLeagueApiId);
            if(!isLeagueType(leagueDetailsRequestDto)){
                continue;
            }

            saveInitialLeague(leagueDetailsRequestDto);
            LeagueStandingDto leagueStandingDto = footballApiService.getLeagueStandings(nowLeagueApiId, leagueDetailsRequestDto.getCurrentSeason());

            if(leagueStandingDto.getStandings().isEmpty()){
                // leagueApiId = 447 인 경우 leagueInfo 에서 standing=true 인데 실제 standing 요청시 null 인경우 존재
                continue;
            }

            leagueStandingDto.getStandings().forEach(standing -> {
                saveInitialTeam(leagueDetailsRequestDto.getLeagueApiId(), standing.getTeamApiId(), leagueDetailsRequestDto.getCurrentSeason());
            });
        }
    }

    private Boolean isLeagueType(LeagueDetailsRequestDto leagueDetailsRequestDto) {
        return leagueDetailsRequestDto != null &&
                leagueDetailsRequestDto.getLeagueType().equals(LeagueType.LEAGUE.getValue()) &&
                leagueDetailsRequestDto.getCurrentSeason() >= SEASON_2024 &&
                leagueDetailsRequestDto.getStanding();
    }

    private void saveInitialLeague(LeagueDetailsRequestDto leagueDetailsRequestDto) {
        leagueService.saveByLeagueDetails(leagueDetailsRequestDto);
    }

    private void saveInitialTeam(Integer leagueId, Integer teamId, Integer currentSeason) {
        if(teamRepository.findTeamByTeamApiId(teamId).isPresent()){
            return;
        }
        TeamDetailsDto teamDetailsDto = footballApiService.getTeamDetails(leagueId, teamId, currentSeason);
        teamService.saveByTeamDetails(teamDetailsDto);
    }

//    @Scheduled(cron = "0 0 16 * * ?")    // 초, 분, 시, 일, 월, 요일
//    public void updateLeagueStanding(){
//        for(Integer i = firstLeagueId; i<lastLeagueId+1; i++){
//            LeagueDetailsDto leagueDetailsDto = footballApiService.getLeagueDetails(lastLeagueId);
////            if(leagueRepository.findLeagueByLeagueApiId(leagueDetailsDto.getLeagueApiId()).isEmpty()){
////
////            }
////          leagueDetailsDto.getStandings().stream().map(()
//        }
//
//    }

}
