package com.ksy.fmrs.controller;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.service.PlayerFacadeService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final PlayerFacadeService playerFacadeService;
    private final PlayerService playerService;

    /**
     * 1. player 상세정보
     * 2. 매핑된 상태라면 fmplayer 정보
     * 3. 실제 스탯
     */
    @GetMapping("/players/{playerId}")
    public String getPlayerDetail(@PathVariable Long playerId, Model model) {
        PlayerOverviewDto playerOverviewDto = playerFacadeService.getPlayerOverview(playerId);
        model.addAttribute("player", playerOverviewDto);
        return "player-detail";
    }

    // 상세 검색 결과 반환 페이지
    @GetMapping("/players/detail-search")
    public String search(
            @ModelAttribute SearchPlayerCondition condition,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        // 첫 페이지(조건 모두 null)이면 빈 폼만 보여줌
        if (condition == null) {
            model.addAttribute("searchPlayerCondition", new SearchPlayerCondition());
            return "players-detail-search";
        }

        SearchPlayerResponseDto dto = playerService
                .searchPlayerByDetailCondition(condition, pageable);

        model.addAttribute("players", dto);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("totalPages", dto.getTotalPages());
        model.addAttribute("totalElements", dto.getTotalElements());
        model.addAttribute("searchPlayerCondition", condition);

        return "players-detail-search";
    }

    @ResponseBody
    @GetMapping("/api/search/simple-player/{name}")
    public SearchPlayerResponseDto searchPlayerByName(
            @PathVariable String name,
            Pageable pageable,
            @RequestParam(required = false) Long lastPlayerId,
            @RequestParam(required = false) Integer lastCurrentAbility,
            @RequestParam(required = false) MappingStatus lastMappingStatus
    ) {
        return playerService.searchPlayerByName(name, pageable, lastMappingStatus, lastCurrentAbility, lastPlayerId);
    }

    @ResponseBody
    @PostMapping("/api/search/detail-player")
    public SearchPlayerResponseDto searchPlayerByDetailConditionResult(
            @RequestBody(required = false) SearchPlayerCondition searchPlayerCondition,
            @PageableDefault Pageable pageable
    ) {
        return playerService.searchPlayerByDetailCondition(searchPlayerCondition, pageable);
    }

}