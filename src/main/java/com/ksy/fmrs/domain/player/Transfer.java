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
    private int id;

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

    public Transfer(Player player, Team toTeam, Team fromTeam, TransferType type, Integer fee, LocalDate date, LocalDateTime update) {
        Transfer transfer = new Transfer();
        transfer.player = player;
        transfer.toTeam = toTeam;
        transfer.fromTeam = fromTeam;
        transfer.type = type;
        transfer.fee = fee;
        transfer.date = date;
        transfer.update = update;
    }
}
