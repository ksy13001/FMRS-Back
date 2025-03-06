package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.league.LeagueDetailsResponseDto;
import com.ksy.fmrs.dto.league.LeagueStandingDto;
import com.ksy.fmrs.dto.player.PlayerSimpleDto;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LeagueController {

    private final LeagueService leagueService;
    private final FootballApiService footballApiService;

    @GetMapping("/leagues/{leagueId}")
    public String getLeagueDetails(Model model, @PathVariable Long leagueId) {
        LeagueDetailsResponseDto leagueDetailsResponseDto = leagueService.getLeagueDetails(leagueId);
        Integer leagueApiId = leagueDetailsResponseDto.getLeagueApiId();
        LeagueStandingDto leagueStandingDto = footballApiService.getLeagueStandings(
                leagueApiId,
                leagueDetailsResponseDto.getCurrentSeason());
        List<PlayerSimpleDto> topScorers = footballApiService.getLeagueTopScorers(leagueApiId);
        List<PlayerSimpleDto> topAssistants = footballApiService.getLeagueTopAssists(leagueApiId);

        model.addAttribute("league", leagueDetailsResponseDto);
        model.addAttribute("standing", leagueStandingDto);
        model.addAttribute("topScorers", topScorers);
        model.addAttribute("topAssistants", topAssistants);
        return "league-detail";
    }

//    @ResponseBody
//    @GetMapping("/api/leagues/{leagueId}")
//    public LeagueStandingDto getLeagueDetail(@PathVariable Long leagueId) {
//        return footballApiService.getLeagueStandings(leagueId);
//    }
}
