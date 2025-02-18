package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.SearchPlayerResponseDto;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final PlayerService playerService;

    @GetMapping("/api/home")
    public String home(Model model) {
        SearchPlayerResponseDto searchPlayerResponseDto = playerService.getPlayersByMarketValueDesc();

        model.addAttribute("featuredPlayers", searchPlayerResponseDto);
        return "home";
    }

}
