package com.ksy.fmrs.controller;

import com.ksy.fmrs.service.global.FootballApiService;
import com.ksy.fmrs.service.league.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class LeagueController {

    private final LeagueService leagueService;
    private final FootballApiService footballApiService;

//    @GetMapping("/leagues/{leagueId}")
//    public String getLeagueDetails(Model model, @PathVariable Long leagueId) {
//        LeagueDetailsResponseDto leagueDetailsResponseDto = leagueService.getLeagueDetails(leagueId);
//        Integer leagueApiId = leagueDetailsResponseDto.getLeagueApiId();
////        LeagueStandingDto leagueStandingDto = footballApiService.getLeagueStandings(
////                leagueApiId,
////                leagueDetailsResponseDto.getCurrentSeason()).block();
//        List<PlayerSimpleDto> topScorers = footballApiService.getLeagueTopScorers(leagueApiId);
//        List<PlayerSimpleDto> topAssistants = footballApiService.getLeagueTopAssists(leagueApiId);
//
//        model.addAttribute("league", leagueDetailsResponseDto);
////        model.addAttribute("standing", leagueStandingDto);
//        model.addAttribute("topScorers", topScorers);
//        model.addAttribute("topAssistants", topAssistants);
//        return "league-detail";
//    }

//    @ResponseBody
//    @GetMapping("/api/leagues/{leagueId}")
//    public LeagueStandingDto getLeagueDetail(@PathVariable Long leagueId) {
//        return footballApiService.getLeagueStandings(leagueId);
//    }
}
