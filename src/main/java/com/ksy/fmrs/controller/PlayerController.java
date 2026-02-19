package com.ksy.fmrs.controller;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.dto.ApiResponse;
import com.ksy.fmrs.dto.nation.NationDto;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.search.DetailSearchPlayerResultDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SimpleSearchPlayerResultDto;
import com.ksy.fmrs.service.PlayerFacadeService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PlayerController {

    private final PlayerFacadeService playerFacadeService;
    private final PlayerService playerService;

    @GetMapping("/api/players/{playerId}")
    public ResponseEntity<ApiResponse<PlayerOverviewDto>> getPlayerDetail(@PathVariable Long playerId) {
        return ApiResponse.ok(playerFacadeService.getPlayerOverview(playerId), "player details success");
    }

    @GetMapping("/api/search/simple-player/{name}")
    public ResponseEntity<ApiResponse<SimpleSearchPlayerResultDto>> searchPlayerByName(
            @PathVariable String name,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long lastPlayerId,
            @RequestParam(required = false) Integer lastCurrentAbility,
            @RequestParam(required = false) MappingStatus lastMappingStatus
    ) {
        return ApiResponse.ok(
                playerService.simpleSearchPlayers(name, pageable, lastPlayerId, lastCurrentAbility, lastMappingStatus),
                "simple player details success");
    }

    @GetMapping("/api/search/detail-player")
    public ResponseEntity<ApiResponse<DetailSearchPlayerResultDto>> searchPlayerByDetailConditionResult(
            @ModelAttribute SearchPlayerCondition searchPlayerCondition,
            @PageableDefault Pageable pageable
    ) {
        return ApiResponse.ok(playerService.detailSearchPlayers(searchPlayerCondition, pageable),
                "detail player details success");
    }

    @GetMapping("/api/nations")
    public ResponseEntity<ApiResponse<List<NationDto>>> getNationsFromPlayers() {
        return ApiResponse.ok(playerService.getNationsFromPlayers(),
                "nations from players success");
    }
}