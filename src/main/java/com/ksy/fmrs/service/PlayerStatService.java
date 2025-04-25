package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlayerStatService {

    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;

//    public void savePlayerStat(){
//        playerStatRepository.save();
//    }

    public PlayerStatDto getPlayerStatByPlayerId(Long playerId){
        Player player = playerRepository.findById(playerId)
                .orElseThrow(()-> new EntityNotFoundException("Player not found. id: " + playerId));
        return new PlayerStatDto(player.getPlayerStat());
    }

}
