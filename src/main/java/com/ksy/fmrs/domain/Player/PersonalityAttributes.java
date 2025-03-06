package com.ksy.fmrs.domain.Player;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class PersonalityAttributes {
    private int Adaptability;
    private int Ambition;
    private int Loyalty;
    private int Pressure;
    private int Professional;
    private int Sportsmanship;
    private int Temperament;
    private int Controversy;
}