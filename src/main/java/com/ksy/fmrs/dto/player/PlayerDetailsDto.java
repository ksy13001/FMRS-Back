package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlayerDetailsDto {


    public PlayerDetailsDto(Player player, String teamName, String teamLogoUrl, Integer currentAbility) {
        this.id = player.getId();
        this.playerApiId = player.getPlayerApiId();
        this.name = player.getName();
        this.birth = player.getBirth();
        this.age = player.getAge();
        this.height = player.getHeight();
        this.weight = player.getWeight();
        this.teamName = teamName;
        this.teamLogoUrl = teamLogoUrl;
        this.nationName = player.getNationName();
        this.nationLogoUrl = player.getNationLogoUrl();
        this.imageUrl = player.getImageUrl();
        this.mappingStatus = player.getMappingStatus();
        this.currentAbility = currentAbility;
    }

    private Long id;
    private Integer playerApiId;
    private String name;
    private LocalDate birth;
    private int age;
    private int height;
    private int weight;
    private String teamName;
    private String teamLogoUrl;
    private String nationName;
    private String nationLogoUrl;
    private String imageUrl;
    private MappingStatus mappingStatus;
    private Integer currentAbility;
}
