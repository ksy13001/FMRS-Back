package com.ksy.fmrs.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String refreshJti;

    private Instant expiryDate;

    @Builder
    public BlackList(Long userId, String refreshJti, Instant expiryDate) {
        this.userId = userId;
        this.refreshJti = refreshJti;
        this.expiryDate = expiryDate;
    }
}
