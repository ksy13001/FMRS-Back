package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.LeagueDetailsDto;
import com.ksy.fmrs.dto.PlayerSimpleDto;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LeagueController {

    private final FootballApiService footballApiService;

    @GetMapping("/leagues/{leagueId}")
    public String getLeagueDetail(Model model, @PathVariable Integer leagueId) {
        LeagueDetailsDto leagueDetailsDto = footballApiService.getLeagueDetails(leagueId);
        List<PlayerSimpleDto> topScorers = footballApiService.getLeagueTopScorers(leagueId);
        List<PlayerSimpleDto> topAssistants = footballApiService.getLeagueTopAssists(leagueId);

        model.addAttribute("league", leagueDetailsDto);
        model.addAttribute("topScorers", topScorers);
        model.addAttribute("topAssistants", topAssistants);
        return "league-detail";
    }

    @ResponseBody
    @GetMapping("/api/leagues/{leagueId}")
    public LeagueDetailsDto getLeagueDetail(@PathVariable Integer leagueId) {
        return footballApiService.getLeagueDetails(leagueId);
    }
}
