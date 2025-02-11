package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.dto.SearchPlayerResponse;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/api/search/simple-players")
    public SearchPlayerResponse searchPlayerByName(@RequestParam String name) {
        return playerService.searchPlayerByName(name);
    }

    @GetMapping("/api/search/detail-players")
    public SearchPlayerResponse searchPlayerByDetailCondition(@RequestBody SearchPlayerCondition condition) {
        return playerService.searchPlayerByDetailCondition(condition);
    }
}

