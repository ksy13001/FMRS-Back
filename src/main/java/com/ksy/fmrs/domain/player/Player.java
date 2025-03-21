package com.ksy.fmrs.domain.player;


import com.ksy.fmrs.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_api_id")
    private Integer playerApiId;

    @Column(name = "team_api_id")
    private Integer teamApiId;

    @Column(name="league_api_id")
    private Integer leagueApiId;

    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    private int height;

    private int weight;

    private int age;

    @Column(name = "nation_name")
    private String nationName;

    @Column(name = "nation_logo_url")
    private String nationLogoUrl;

    @Column(name = "market_value")
    private int marketValue;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private Position position;

    @Column(name = "current_ability")
    private int currentAbility;

    @Column(name = "potential_ability")
    private int potentialAbility;

    // 인성
    @Column(name = "personality_attributes")
    @Embedded
    private PersonalityAttributes personalityAttributes;

    // 기술(Technical) 능력치
    @Column(name = "technical_attributes")
    @Embedded
    private TechnicalAttributes technicalAttributes;

    // 정신(Mental) 능력치
    @Column(name = "mental_attributes")
    @Embedded
    private MentalAttributes mentalAttributes;

    // 신체(Physical) 능력치
    @Column(name = "physical_attributes")
    @Embedded
    private PhysicalAttributes physicalAttributes;

    // 골키퍼 능력치
    @Column(name = "goalKeeper_attributes")
    @Embedded
    private GoalKeeperAttributes goalKeeperAttributes;

    // 히든 능력치
    @Column(name = "hidden_attributes")
    @Embedded
    private HiddenAttributes hiddenAttributes;

    @Builder
    public Player(
            String name,
            Integer playerApiId,
            String firstName,
            String lastName,
            LocalDate birth,
            int age,
            int height,
            int weight,
            int marketValue,
            Integer teamApiId,
            Integer leagueApiId,
            String imageUrl,
            Position position,
            String nationName,
            String nationLogoUrl,
            PersonalityAttributes personalityAttributes,
            TechnicalAttributes technicalAttributes,
            MentalAttributes mentalAttributes,
            PhysicalAttributes physicalAttributes,
            GoalKeeperAttributes goalKeeperAttributes,
            HiddenAttributes hiddenAttributes,
            int currentAbility,
            int potentialAbility
    ) {
        this.name = name;
        this.playerApiId = playerApiId;
        this.teamApiId = teamApiId;
        this.leagueApiId = leagueApiId;
        this.age = age;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.marketValue = marketValue;
        this.position = position;
        this.imageUrl = imageUrl;
        this.nationName = nationName;
        this.nationLogoUrl = nationLogoUrl;
        this.personalityAttributes = personalityAttributes;
        this.technicalAttributes = technicalAttributes;
        this.mentalAttributes = mentalAttributes;
        this.physicalAttributes = physicalAttributes;
        this.goalKeeperAttributes = goalKeeperAttributes;
        this.hiddenAttributes = hiddenAttributes;
        this.currentAbility = currentAbility;
        this.potentialAbility = potentialAbility;
    }

    // 연관관계 설정 메서드
    public void updateTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }

    public void updatePlayerApiId(Integer playerApiId) {
        this.playerApiId = playerApiId;
    }

    public void updateMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateFmData(
            PersonalityAttributes personalityAttributes,
            TechnicalAttributes technicalAttributes,
            MentalAttributes mentalAttributes,
            PhysicalAttributes physicalAttributes,
            GoalKeeperAttributes goalKeeperAttributes,
            HiddenAttributes hiddenAttributes,
            int currentAbility,
            int potentialAbility) {
        this.personalityAttributes = personalityAttributes;
        this.technicalAttributes = technicalAttributes;
        this.mentalAttributes = mentalAttributes;
        this.physicalAttributes = physicalAttributes;
        this.goalKeeperAttributes = goalKeeperAttributes;
        this.hiddenAttributes = hiddenAttributes;
        this.currentAbility = currentAbility;
        this.potentialAbility = potentialAbility;
    }
}
