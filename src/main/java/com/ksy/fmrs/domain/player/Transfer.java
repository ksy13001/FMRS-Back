package com.ksy.fmrs.domain.player;


import com.ksy.fmrs.domain.BaseTime;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.TransferType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Transfer extends BaseTime {

    protected Transfer() {}

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_team_id")
    private Team toTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_team_id")
    private Team fromTeam;

    @Enumerated(EnumType.STRING)
    private TransferType type;

    private Integer fee;

    private LocalDate date;

    private LocalDateTime update;

    protected Transfer(Player player, Team toTeam, Team fromTeam, TransferType type, Integer fee, LocalDate date, LocalDateTime update) {
        this.player = player;
        this.toTeam = toTeam;
        this.fromTeam = fromTeam;
        this.type = type;
        this.fee = fee;
        this.date = date;
        this.update = update;
    }
}
