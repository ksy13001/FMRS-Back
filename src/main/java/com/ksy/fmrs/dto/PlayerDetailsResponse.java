package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDetailsResponse {

    public PlayerDetailsResponse(Player player) {
        this.name = player.getName();
    }

    private String name;
}
