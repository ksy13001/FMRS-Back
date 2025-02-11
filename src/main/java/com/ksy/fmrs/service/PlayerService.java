package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.PlayerDetailsResponse;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.dto.SearchPlayerResponse;
import com.ksy.fmrs.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public SearchPlayerResponse searchPlayerByName(String name) {
        return new SearchPlayerResponse(playerRepository.searchPlayerByName(name)
                .stream()
                .map(PlayerDetailsResponse::new)
                .toList());
    }

    public SearchPlayerResponse searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponse(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(PlayerDetailsResponse::new)
                .toList());
    }
}
