package com.ksy.fmrs.dto.player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SquadPlayerDto {
    private Integer playerApiId;
    private String name;
    private int age;
    private String imageUrl;
}
