package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.dto.LeagueDetailsDto;
import com.ksy.fmrs.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    @Transactional
    public void saveByLeagueDetails(LeagueDetailsDto leagueDetailsDto) {
        League league = League.builder()
                .leagueApiId(leagueDetailsDto.getLeagueApiId())
                .name(leagueDetailsDto.getName())
                .nation(leagueDetailsDto.getNationName())
                .nationImageUrl(leagueDetailsDto.getNationImageUrl())
                .currentSeason(leagueDetailsDto.getCurrentSeason())
                .logoUrl(leagueDetailsDto.getLogoImageUrl())
                .build();

        leagueRepository.save(league);
    }

}
