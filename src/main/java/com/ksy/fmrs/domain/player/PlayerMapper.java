package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
import com.ksy.fmrs.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PlayerMapper {

    public List<Player> leaguePlayersToEntities(LeagueApiPlayersDto leagueApiPlayersDto) {
        return leagueApiPlayersDto.response().stream().filter(Objects::nonNull)
                .map(dto -> {
                    LeagueApiPlayersDto.PlayerDto player = dto.player();
                    return Player.builder()
                            .playerApiId(player.id())
                            .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                            .lastName(StringUtils.getLastName(player.name()).toUpperCase())
                            .birth(player.birth().date())
                            .height(StringUtils.extractNumber(player.height()))
                            .weight(StringUtils.extractNumber(player.weight()))
                            .imageUrl(player.photo())
                            .nationName(player.nationality().toUpperCase())
                            .mappingStatus(PlayerMappingStatus.UNMAPPED)
                            .build();
                }).toList();
    }

}
