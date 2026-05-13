package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;

import java.time.LocalDate;

public record BirthNationKey(
        LocalDate birth,
        String nationName
) {
    public static BirthNationKey from(Player player) {
        return new BirthNationKey(player.getBirth(), player.getNationName());
    }

    public static BirthNationKey from(FmPlayer fmPlayer) {
        return new BirthNationKey(fmPlayer.getBirth(), fmPlayer.getNationName());
    }

    public boolean isComplete() {
        return birth != null && nationName != null && !nationName.isEmpty();
    }
}
