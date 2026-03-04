package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.ApiResponse;
import com.ksy.fmrs.dto.player.PlayerStatResponse;
import com.ksy.fmrs.service.PlayerStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PlayerStatController {
    private final PlayerStatService playerStatService;

    @PostMapping("/api/players/{playerId}/stats/refresh")
    public ResponseEntity<ApiResponse<PlayerStatResponse>> refreshPlayerStats(@PathVariable Long playerId) {
        return ApiResponse.ok(playerStatService.savePlayerStat(playerId),
                "PlayerStat refresh success");
    }

}
