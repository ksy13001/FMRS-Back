package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.dto.apiFootball.TeamListApiResponseDto;
import com.ksy.fmrs.dto.league.LeagueAPIDetailsResponseDto;
import com.ksy.fmrs.mapper.ApiDtoMapper;
import com.ksy.fmrs.repository.BulkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitializationService {
    private final FootballApiService footballApiService;
    private final ApiDtoMapper apiDtoMapper;
    private final BulkRepository bulkRepository;

    @Transactional
    public void saveInitialTeams(List<League> leagues) {
        Map<Integer, Long> apiIdToId = leagues.stream()
                .collect(Collectors.toMap(League::getLeagueApiId, League::getId));

        leagues.stream()
                .map(league -> {
                    log.info("league id ={}, name ={} 의 팀 update 시작", league.getId(), league.getName());
                    return footballApiService
                            .getTeamsInLeague(
                                    league.getLeagueApiId(),
                                    league.getCurrentSeason()).block();
                })
                .forEach(dto -> {
                    Integer leagueApiId = parsingLeagueApiId(dto);
                    bulkRepository.bulkUpsertTeams(
                            apiDtoMapper.toEntity(dto),
                            apiIdToId.get(parsingLeagueApiId(dto)));
                    log.info("리그 apiId= {} 팀 업데이트", leagueApiId);
                });
    }

    private Integer parsingLeagueApiId(TeamListApiResponseDto dto) {
        Integer LeagueApiId = null;
        try {
            LeagueApiId = Integer.valueOf(dto.parameters().league());
        } catch (NullPointerException e) {
            log.error("League api id is null");
        }
        return LeagueApiId;
    }

}
