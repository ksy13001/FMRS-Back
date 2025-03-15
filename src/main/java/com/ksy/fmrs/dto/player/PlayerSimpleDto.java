package com.ksy.fmrs.dto.player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayerSimpleDto {
    Integer teamApiId;
    Integer playerApiId;
    String name;
    String teamName;
    Integer age;
    Integer goal;
    Integer assist;
    String rating;
    String imageUrl;
}
