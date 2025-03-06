package com.ksy.fmrs.domain.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class MentalAttributes {
    private int aggression;
    private int anticipation;
    private int bravery;
    private int composure;
    private int concentration;
    private int decisions;
    private int determination;
    private int flair;
    private int leadership;

    @Column(name = "off_the_ball")
    private int offTheBall;

    private int positioning;
    private int teamwork;
    private int vision;

    @Column(name = "work_rate")
    private int workRate;
}
