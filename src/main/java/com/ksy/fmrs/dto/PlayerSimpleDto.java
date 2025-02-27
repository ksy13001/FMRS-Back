package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayerSimpleDto {
    String name;
    String teamName;
    Integer age;
    Integer goal;
    Integer assist;
    String rating;
    String imageUrl;
}
