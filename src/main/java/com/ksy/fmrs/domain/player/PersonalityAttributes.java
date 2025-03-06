package com.ksy.fmrs.domain.player;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class PersonalityAttributes {
    private int adaptability;
    private int ambition;
    private int loyalty;
    private int pressure;
    private int professional;
    private int sportsmanship;
    private int temperament;
    private int controversy;
}