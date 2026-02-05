package com.ksy.fmrs.domain;


import com.ksy.fmrs.domain.enums.TransferType;
import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(uniqueConstraints = @UniqueConstraint(
        name = "unique_transfer",
        columnNames = {"player_id", "from_team_id", "to_team_id", "date"}
    )
)
@Getter
@Entity
public class Transfer extends BaseTime {

    protected Transfer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_team_id", nullable = false)
    private Team toTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_team_id", nullable = false)
    private Team fromTeam;

    @Enumerated(EnumType.STRING)
    private TransferType type;

    private Double fee;

    @Column(length = 8)
    private String currency;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transfer(Player player, Team fromTeam, Team toTeam, TransferType type, Double fee, String currency, LocalDate date, LocalDateTime updatedAt) {
        this.player = player;
        this.toTeam = toTeam;
        this.fromTeam = fromTeam;
        this.type = type;
        this.fee = fee;
        this.currency = currency;
        this.date = date;
        this.updatedAt = updatedAt;
    }
}
