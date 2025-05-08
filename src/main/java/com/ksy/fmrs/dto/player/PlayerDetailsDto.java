package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.player.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlayerDetailsDto {


    public PlayerDetailsDto(Player player, String teamName) {
        this.id = player.getId();
        this.playerApiId = player.getPlayerApiId();
        this.name = player.getName();
        this.birth = player.getBirth();
        this.age = player.getAge();
        this.height = player.getHeight();
        this.weight = player.getWeight();
        this.teamName = teamName;
        this.nationName = player.getNationName();
        this.nationLogoUrl = player.getNationLogoUrl();
        this.imageUrl = player.getImageUrl();
    }

    private Long id;
    private Integer playerApiId;
    private String name;
    private LocalDate birth;
    private int age;
    private int height;
    private int weight;
    private String teamName;
    private String nationName;
    private String nationLogoUrl;
    private String imageUrl;
}
