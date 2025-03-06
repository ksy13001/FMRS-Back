package com.ksy.fmrs.domain.Player;


import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.PositionEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer playerApiId;

    private String name;

    private LocalDate birth;

    private int age;

    private int height;

    private int weight;

    private String nationName;

    private String nationLogoUrl;

    @Column(name = "market_value")
    private int marketValue;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String imageUrl;

    @Embedded
    private Position position;

    // 인성
    @Embedded
    private PersonalityAttributes personalityAttributes;

    // 기술(Technical) 능력치
    @Embedded
    private TechnicalAttributes technicalAttributes;

    // 정신(Mental) 능력치
    @Embedded
    private MentalAttributes mentalAttributes;

    // 신체(Physical) 능력치
    @Embedded
    private PhysicalAttributes physicalAttributes;

    // 골키퍼 능력치
    @Embedded
    private GoalKeeperAttributes goalKeeperAttributes;

    // 히든 능력치
    @Embedded
    private HiddenAttributes hiddenAttributes;

    @Builder
    public Player(
            String name,
            LocalDate birth,
            int age,
            int height,
            int weight,
            int marketValue,
            String imageUrl,
            Position position,
            PersonalityAttributes personalityAttributes,
            TechnicalAttributes technicalAttributes,
            MentalAttributes mentalAttributes,
            PhysicalAttributes physicalAttributes,
            GoalKeeperAttributes goalKeeperAttributes,
            HiddenAttributes hiddenAttributes
    ) {
        this.name = name;
        this.birth = birth;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.marketValue = marketValue;
        this.position = position;
        this.imageUrl = imageUrl;
        this.personalityAttributes = personalityAttributes;
        this.technicalAttributes = technicalAttributes;
        this.mentalAttributes = mentalAttributes;
        this.physicalAttributes = physicalAttributes;
        this.goalKeeperAttributes = goalKeeperAttributes;
        this.hiddenAttributes = hiddenAttributes;
    }

    // 연관관계 설정 메서드
    public void updateTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }

    public void updateMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
