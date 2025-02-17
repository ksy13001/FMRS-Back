package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.*;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final FootballApiService footballApiService;

    @GetMapping("/api/players/{playerId}")
    public String getPlayerProfile(@PathVariable Long playerId, Model model) {
        PlayerDetailsResponseDto playerDetailsResponseDto = playerService.getPlayerDetails(playerId);
        PlayerRealFootballStatDto playerRealFootballStatDto = footballApiService.getPlayerRealStat(
                playerDetailsResponseDto.getName(), playerDetailsResponseDto.getTeamName()
        );
        model.addAttribute("player", playerDetailsResponseDto);
        model.addAttribute("playerRealFootballStat", playerRealFootballStatDto);
        return "player-detail";
    }

    @GetMapping("/api/team/{teamId}/players")
    public TeamPlayersResponseDto getPlayers(@PathVariable Long teamId) {
        return playerService.getTeamPlayersByTeamId(teamId);
    }

    @GetMapping("/api/search/simple-players")
    public SearchPlayerResponseDto searchPlayerByName(@RequestParam String name) {
        return playerService.searchPlayerByName(name);
    }

    // 상세 검색창 반환 페이지
//    @GetMapping("/api/players/detail-search")
//    public String searchPlayerByDetailCondition(@ModelAttribute("searchPlayerCondition") SearchPlayerCondition searchPlayerCondition, Model model) {
//        return "players-detail-search";
//    }
    @GetMapping("/api/players/detail-search")
    public String searchPlayerByDetailCondition(Model model) {
        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition(); // 새로운 객체를 생성하여 모델에 추가
        model.addAttribute("searchPlayerCondition", searchPlayerCondition); // 모델에 추가
        return "players-detail-search";
    }

    // 상세 검색 결과 반환 페이지
    @GetMapping("/api/players/detail-search/result")
    public String searchPlayerByDetailConditionResult(@ModelAttribute("searchPlayerCondition") SearchPlayerCondition searchPlayerCondition, Model model) {
        SearchPlayerResponseDto searchPlayerResponseDto = playerService.searchPlayerByDetailCondition(searchPlayerCondition);
        model.addAttribute("players", searchPlayerResponseDto);
        return "players-detail-search";
    }
}