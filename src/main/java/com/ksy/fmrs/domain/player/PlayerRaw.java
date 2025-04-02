package com.ksy.fmrs.domain.player;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "player_raw")
public class PlayerRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(name = "json_raw", columnDefinition = "MEDIUMTEXT")
    private String jsonRaw;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private boolean processed;


    @Builder
    public PlayerRaw(String jsonRaw, LocalDateTime createdAt, boolean processed) {
        this.jsonRaw = jsonRaw;
        this.createdAt = createdAt;
        this.processed = processed;
    }

    public void updateProcessed(boolean processed) {
        this.processed = processed;
    }
}
