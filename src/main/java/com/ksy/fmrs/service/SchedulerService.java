package com.ksy.fmrs.service;


import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.PlayerStatRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerService {
    private final TeamService teamService;
    private final FootballApiService footballApiService;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;

//


//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void updateSquadForTeam(Team team){
//            // 팀 스쿼드 불러오기
//            PlayerStatisticsApiResponseDto response = footballApiService.getSquadStatistics(
//                    team.getTeamApiId(),
//                    team.getCurrentSeason());
//
//            if(response==null) {
//                return;
//            }
//            // 팀 초기화
//            team.resetSquad();
//
//            // 스쿼드 player 업데이트, 캐싱
//            response.getResponse().forEach(playerWrapperDto -> {// player, statistics
//                PlayerStatisticsApiResponseDto.PlayerDto squadPlayer = playerWrapperDto.getPlayer();
//                PlayerStatisticsApiResponseDto.StatisticDto statistic = playerWrapperDto.getStatistics().getFirst();
//                String lastName = StringUtils.getLastName(squadPlayer.getName());
//                LocalDate birth = StringUtils.parseLocalToString(squadPlayer.getBirth().getDate());
//                String firstChar = StringUtils.getFirstChar(squadPlayer.getName());
//                List<Player> findPlayers = playerRepository.searchPlayerByLastNameAndBirth(lastName, birth, firstChar);
//                if (findPlayers.size() > 1) {
//                    log.info("max_size error----: name:" + squadPlayer.getName());
//                    return;
//                }
//                if (findPlayers.isEmpty()) {
//                    log.info("empty error----: name:" + squadPlayer.getName()+ " team = " + team.getTeamApiId() + " " + team.getName());
//                    return;
//                }
//                findPlayers.getFirst().updateTeam(team);
//                log.info("success-----: name : "+squadPlayer.getName()+" team = "+team.getTeamApiId()+" "+team.getName());
//            });
//    }
}
