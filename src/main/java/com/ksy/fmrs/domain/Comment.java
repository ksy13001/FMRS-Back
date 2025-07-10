package com.ksy.fmrs.domain;

import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.security.Timestamp;

@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Comment extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String author;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}
