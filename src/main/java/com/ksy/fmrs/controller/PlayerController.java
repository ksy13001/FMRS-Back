package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
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

import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final FootballApiService footballApiService;

    /**
     * 1. player 상세정보
     * 2. 매핑된 상태라면 fmplayer 정보
     * 3. 실제 스탯
     * */
    @GetMapping("/players/{playerId}")
    public String getPlayerDetail(@PathVariable Long playerId, Model model) {

        PlayerDetailsDto playerDetailsDto = playerService.getPlayerDetails(playerId);
        Optional<FmPlayerDetailsDto> fmPlayerDetailsDto = playerService.getFmPlayerDetails(playerId);
        PlayerStatDto playerStatDto = footballApiService.savePlayerRealStat(playerId, playerDetailsDto.getPlayerApiId());

        model.addAttribute("player", playerDetailsDto);
        model.addAttribute("fmplayer", fmPlayerDetailsDto.orElse(null));
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