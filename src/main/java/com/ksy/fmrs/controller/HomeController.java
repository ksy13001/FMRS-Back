package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.service.PlayerService;
import com.ksy.fmrs.service.InitializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final PlayerService playerService;
    private final InitializationService initializationService;

    @GetMapping("/home")
    public String home(Model model) {
        SearchPlayerResponseDto searchPlayerResponseDto = playerService.getPlayersByMarketValueDesc();

        model.addAttribute("featuredPlayers", searchPlayerResponseDto);
        return "home";
    }

    @ResponseBody
    @PostMapping("/api/insert-league-team-data")
    public void insertInitialLeagueTeamData() {
        initializationService.createInitialLeague();
    }
}
