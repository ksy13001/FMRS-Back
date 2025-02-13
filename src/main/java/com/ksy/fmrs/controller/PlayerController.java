package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.PlayerDetailsResponseDto;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.dto.SearchPlayerResponseDto;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/api/players/{playerId}")
    public String getPlayerProfile(@PathVariable Long playerId, Model model) {
        PlayerDetailsResponseDto player = playerService.getPlayerDetails(playerId);
        model.addAttribute("player", player);
        return "player-detail";
    }

//
//    @GetMapping("/api/team/{teamId}/players")
//    public TeamPlayersResponseDto getPlayers(@PathVariable Long teamId) {
//        return playerService.getTeamPlayersByTeamId(teamId);
//    }
//
//    @GetMapping("/api/search/simple-players")
//    public SearchPlayerResponseDto searchPlayerByName(@RequestParam String name) {
//        return playerService.searchPlayerByName(name);
//    }
//
    @GetMapping("/api/search/detail-players")
    public SearchPlayerResponseDto searchPlayerByDetailCondition(@RequestBody SearchPlayerCondition condition) {
        return playerService.searchPlayerByDetailCondition(condition);
    }
}

