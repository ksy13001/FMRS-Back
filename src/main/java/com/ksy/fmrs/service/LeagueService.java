package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.league.LeagueAPIDetailsResponseDto;
import com.ksy.fmrs.dto.league.LeagueDetailsResponseDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeagueService {

    private final FootballApiService footballApiService;
    private final TimeProvider timeProvider;
    private final LeagueRepository leagueRepository;

    @Transactional
    public void save(League league) {
        leagueRepository.save(league);
    }

    @Transactional
    public void upsert(League league){
        leagueRepository.upsertLeague(
                league.getLeagueApiId(),
                league.getName(),
                league.getLogoUrl(),
                league.getCurrentSeason(),
                league.getStanding(),
                league.getStartDate(),
                league.getEndDate(),
                league.getLeagueType().getValue()
        );
    }

    @Transactional
    public void saveAllByLeagueDetails(List<LeagueAPIDetailsResponseDto> leagueAPIDetailsResponseDto) {
        List<League> allLeague = leagueAPIDetailsResponseDto.stream().map(dto -> {
            return League.builder()
                    .leagueApiId(dto.getLeagueApiId())
                    .name(dto.getLeagueName())
                    .nationName(dto.getNationName())
                    .nationLogoUrl(dto.getNationImageUrl())
                    .currentSeason(dto.getCurrentSeason())
                    .logoUrl(dto.getLogoImageUrl())
                    .leagueType(validateLeagueType(dto.getLeagueType()))
                    .standing(dto.getStanding())
                    .build();
        }).toList();

        leagueRepository.saveAll(allLeague);
    }

    public Optional<LeagueAPIDetailsResponseDto> findLeagueApiInfo(Integer leagueApiId){
        return footballApiService
                .getLeagueInfo(leagueApiId)
                .blockOptional()
                .orElseThrow(() -> new IllegalArgumentException("Api 응답 없음"));
    }

    @Transactional
    public void refreshLeagueSeason(Integer leagueApiId, LeagueAPIDetailsResponseDto dto) {
        League league = leagueRepository.findLeagueByLeagueApiId(leagueApiId)
                .orElseThrow(() -> new IllegalArgumentException("League not found apiId: " + leagueApiId));

        league.updateSeason(dto.getStartDate(), dto.getEndDate(), dto.getCurrentSeason());
    }

    @Transactional(readOnly = true)
    public List<Integer> findLeaguesApiIdsOutsideSeason() {
        return leagueRepository
                .findLeaguesApiIdsOutsideSeason(timeProvider.getCurrentLocalDate());
    }

    public LeagueDetailsResponseDto getLeagueDetails(Long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalArgumentException("League not found id: " + leagueId));
        return createLeagueDetailsResponseDtoByLeague(league);
    }

    private LeagueDetailsResponseDto createLeagueDetailsResponseDtoByLeague(League league) {
        return LeagueDetailsResponseDto.builder()
                .leagueApiId(league.getLeagueApiId())
                .name(league.getName())
                .leagueType(league.getLeagueType())
                .logoImageUrl(league.getLogoUrl())
                .currentSeason(league.getCurrentSeason())
                .nationName(league.getNationName())
                .nationImageUrl(league.getNationLogoUrl())
                .Standing(league.getStanding())
                .build();
    }

    private LeagueType validateLeagueType(String leagueType) {
        if (leagueType == null) {
            throw new IllegalArgumentException("League type is null");
        }
        if (leagueType.equals(LeagueType.LEAGUE.getValue())) {
            return LeagueType.LEAGUE;
        }
        return LeagueType.CUP;
    }
}
