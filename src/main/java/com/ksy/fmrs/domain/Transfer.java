package com.ksy.fmrs.domain;


import com.ksy.fmrs.domain.enums.TransferType;
import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Transfer extends BaseTime{

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private TransferType type;

    private Integer fee;

    private LocalDate date;
}
