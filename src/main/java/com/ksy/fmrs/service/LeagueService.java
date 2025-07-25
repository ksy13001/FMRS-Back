package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.league.LeagueDetailsResponseDto;
import com.ksy.fmrs.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Transactional
    public void saveAllByLeagueDetails(List<LeagueDetailsRequestDto> leagueDetailsRequestDto) {
        List<League> allLeague = leagueDetailsRequestDto.stream().map(dto->{
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
