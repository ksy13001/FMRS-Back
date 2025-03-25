package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.util.FmUtils;
import com.ksy.fmrs.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FmPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    @Column(name = "nation_name")
    private String nationName;

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
    public FmPlayer(
            String name,
            String firstName,
            String lastName,
            LocalDate birth,
            String nationName,
            Position position,
            PersonalityAttributes personalityAttributes,
            TechnicalAttributes technicalAttributes,
            MentalAttributes mentalAttributes,
            PhysicalAttributes physicalAttributes,
            GoalKeeperAttributes goalKeeperAttributes,
            HiddenAttributes hiddenAttributes,
            int currentAbility,
            int potentialAbility) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.nationName = nationName;
        this.position = position;
        this.personalityAttributes = personalityAttributes;
        this.technicalAttributes = technicalAttributes;
        this.mentalAttributes = mentalAttributes;
        this.physicalAttributes = physicalAttributes;
        this.goalKeeperAttributes = goalKeeperAttributes;
        this.hiddenAttributes = hiddenAttributes;
        this.currentAbility = currentAbility;
        this.potentialAbility = potentialAbility;
    }

    public static FmPlayer FmPlayerDtoToEntity(FmPlayerDto fmPlayerDto) {
        String name = fmPlayerDto.getName();
        return FmPlayer.builder()
                .name(name)
                .firstName(StringUtils.getFirstName(name))
                .lastName(StringUtils.getLastName(name))
                .birth(fmPlayerDto.getBorn())
                .nationName(fmPlayerDto.getNation().getShortName())
                .personalityAttributes(FmUtils.getPersonalityAttributesFromFmPlayer(fmPlayerDto))
                .hiddenAttributes(FmUtils.getHiddenAttributesFromFmPlayer(fmPlayerDto))
                .mentalAttributes(FmUtils.getMentalAttributesFromFmPlayer(fmPlayerDto))
                .physicalAttributes(FmUtils.getPhysicalAttributesFromFmPlayer(fmPlayerDto))
                .goalKeeperAttributes(FmUtils.getGoalKeeperAttributesFromFmPlayer(fmPlayerDto))
                .technicalAttributes(FmUtils.getTechnicalAttributesFromFmPlayer(fmPlayerDto))
                .position(FmUtils.getPositionFromFmPlayer(fmPlayerDto))
                .currentAbility(fmPlayerDto.getCurrentAbility())
                .potentialAbility(fmPlayerDto.getPotentialAbility())
                .build();
    }
}
