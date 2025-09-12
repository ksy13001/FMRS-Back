package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.domain.player.Player;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class DetailSearchPlayerResponseDto {
    private Long id;
    private String name;
    private String teamName;
    private String teamLogoUrl;
    private String nationName;
    private Integer age;
    private Integer currentAbility;
    private String imageUrl;
    private List<String> topAttributes;

    public DetailSearchPlayerResponseDto(Player player, String teamName, String teamLogoUrl, Integer currentAbility, List<String> topAttributes) {
        this.id = player.getId();
        this.name = player.getName();
        this.teamName = teamName;
        this.teamLogoUrl = teamLogoUrl;
        this.nationName = player.getNationName();
        this.age = player.getAge();
        this.currentAbility = currentAbility;
        this.imageUrl = player.getImageUrl();
        this.topAttributes = topAttributes;
    }

    @QueryProjection
    public DetailSearchPlayerResponseDto(String teamName, Long id, String name, String teamLogoUrl, String nationName, String imageUrl, Integer currentAbility, Integer age) {
        this.teamName = teamName;
        this.id = id;
        this.name = name;
        this.teamLogoUrl = teamLogoUrl;
        this.nationName = nationName;
        this.imageUrl = imageUrl;
        this.currentAbility = currentAbility;
        this.age = age;
    }
}
