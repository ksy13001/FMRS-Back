package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.dto.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.TeamPlayersResponseDto;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/api/team/{teamId}/players")
    public TeamPlayersResponseDto getPlayers(@PathVariable Long teamId) {
        return playerService.getTeamPlayersByTeamId(teamId);
    }

    @GetMapping("/api/search/simple-players")
    public SearchPlayerResponseDto searchPlayerByName(@RequestParam String name) {
        return playerService.searchPlayerByName(name);
    }

    @GetMapping("/api/search/detail-players")
    public SearchPlayerResponseDto searchPlayerByDetailCondition(@RequestBody SearchPlayerCondition condition) {
        return playerService.searchPlayerByDetailCondition(condition);
    }
}

