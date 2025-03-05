package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.LeagueDetailsResponseDto;
import com.ksy.fmrs.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Transactional
    public void saveByLeagueDetails(LeagueDetailsRequestDto leagueDetailsRequestDto) {
        League league = League.builder()
                .leagueApiId(leagueDetailsRequestDto.getLeagueApiId())
                .name(leagueDetailsRequestDto.getLeagueName())
                .nationName(leagueDetailsRequestDto.getNationName())
                .nationLogoUrl(leagueDetailsRequestDto.getNationImageUrl())
                .currentSeason(leagueDetailsRequestDto.getCurrentSeason())
                .logoUrl(leagueDetailsRequestDto.getLogoImageUrl())
                .leagueType(validateLeagueType(leagueDetailsRequestDto.getLeagueType()))
                .standing(leagueDetailsRequestDto.getStanding())
                .build();

        leagueRepository.save(league);
    }

    public LeagueDetailsResponseDto getLeagueDetails(Long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(()-> new IllegalArgumentException("League not found id: " + leagueId));
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
        if(leagueType==null){
            throw new IllegalArgumentException("League type is null");
        }
        if(leagueType.equals(LeagueType.LEAGUE.getValue())){
            return LeagueType.LEAGUE;
        }
        return LeagueType.CUP;
    }
}
