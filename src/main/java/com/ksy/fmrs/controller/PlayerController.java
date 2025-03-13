package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
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

    @GetMapping("/players/{playerId}")
    public String getPlayerDetail(@PathVariable Long playerId, Model model) {
        PlayerDetailsDto playerDetailsResponseDto = playerService.getPlayerDetails(playerId);
        PlayerStatDto playerStatDto = footballApiService.savePlayerRealStat(
                playerDetailsResponseDto.getId(),
                playerDetailsResponseDto.getPlayerApiId()
        );
        model.addAttribute("player", playerDetailsResponseDto);
        model.addAttribute("realFootballStat", playerStatDto);
        return "player-detail";
    }

    @ResponseBody
    @GetMapping("/api/team/{teamId}/players")
    public TeamPlayersResponseDto getPlayers(@PathVariable Long teamId) {
        return playerService.getTeamPlayersByTeamId(teamId);
    }

    @ResponseBody
    @GetMapping("/api/search/simple-players")
    public SearchPlayerResponseDto searchPlayerByName(@RequestParam String name) {
        return playerService.searchPlayerByName(name);
    }

    // 상세 검색창 반환 페이지
//    @GetMapping("/api/players/detail-search")
//    public String searchPlayerByDetailCondition(@ModelAttribute("searchPlayerCondition") SearchPlayerCondition searchPlayerCondition, Model model) {
//        return "players-detail-search";
//    }

    @GetMapping("/players/detail-search")
    public String searchPlayerByDetailCondition(Model model) {
        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition(); // 새로운 객체를 생성하여 모델에 추가
        model.addAttribute("searchPlayerCondition", searchPlayerCondition); // 모델에 추가
        return "players-detail-search";
    }

    // 상세 검색 결과 반환 페이지
    @GetMapping("/players/detail-search/result")
    public String searchPlayerByDetailConditionResult(@ModelAttribute("searchPlayerCondition") SearchPlayerCondition searchPlayerCondition, Model model) {
        SearchPlayerResponseDto searchPlayerResponseDto = playerService.searchPlayerByDetailCondition(searchPlayerCondition);
        model.addAttribute("players", searchPlayerResponseDto);
        return "players-detail-search";
    }

}