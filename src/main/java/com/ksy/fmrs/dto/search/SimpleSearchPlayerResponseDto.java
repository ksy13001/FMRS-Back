package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleSearchPlayerResponseDto {
    private Long id;
    private String name;
    private String teamName;
    private String teamLogoUrl;
    private String nationName;
    private Integer age;
    private Integer currentAbility;
    private MappingStatus  mappingStatus;
    private String imageUrl;

    public SimpleSearchPlayerResponseDto(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.teamName = player.getTeamName();
        this.teamLogoUrl = player.getTeamLogoUrl();
        this.nationName = player.getNationName();
        this.age = player.getAge();
        this.currentAbility = player.getLatestCurrentAbility();
        this.mappingStatus = player.getMappingStatus();
        this.imageUrl = player.getImageUrl();
    }
}
