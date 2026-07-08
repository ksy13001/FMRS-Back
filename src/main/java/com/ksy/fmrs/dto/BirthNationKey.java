package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.util.StringUtils;

import java.time.LocalDate;

public record BirthNationKey(
        LocalDate birth,
        String nationName
) {
    // nation_name 은 player("England")와 fmplayer("ENGLAND")의 표기가 달라
    // 정규화 없이는 Java 키 비교에서 후보가 전부 탈락한다 (DB 조회는 _ci collation이라 무관)
    public static BirthNationKey from(Player player) {
        return new BirthNationKey(player.getBirth(), StringUtils.normalizeName(player.getNationName()));
    }

    public static BirthNationKey from(FmPlayer fmPlayer) {
        return new BirthNationKey(fmPlayer.getBirth(), StringUtils.normalizeName(fmPlayer.getNationName()));
    }

    public boolean isComplete() {
        return birth != null && nationName != null && !nationName.isEmpty();
    }
}
