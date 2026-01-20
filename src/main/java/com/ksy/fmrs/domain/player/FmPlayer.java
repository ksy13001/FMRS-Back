package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.enums.MappingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "fmplayer")
@Table(name = "fmplayer", indexes = @Index(name = "idx_first_name_and_last_name_and_birth_and_nation_name",
        columnList = "first_name, last_name, birth, nation_name"),
        uniqueConstraints = @UniqueConstraint(name = "ux_fmplayer_uid_version",
        columnNames = {"fm_uid", "fm_version"})
)
public class FmPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fm_uid", nullable = false)
    private Integer fmUid;

    @Column(name = "fm_version", nullable = false)
    @Enumerated(EnumType.STRING)
    private FmVersion fmVersion;

    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    @Column(name = "nation_name")
    private String nationName;

    @Enumerated(EnumType.STRING)
    private MappingStatus mappingStatus;

    @Embedded
    private Position position;

    @Column(name = "current_ability")
    private Integer currentAbility;

    @Column(name = "potential_ability")
    private Integer potentialAbility;

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
            Integer fmUid,
            String name,
            FmVersion fmVersion,
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
            Integer currentAbility,
            Integer potentialAbility) {
        this.fmUid = fmUid;
        this.name = name;
        this.fmVersion = fmVersion;
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

    public Map<String, Integer> getAllAttributes() {
        Map<String, Integer> allAttributes = new HashMap<>();
        allAttributes.putAll(this.getTechnicalAttributes().getAllTechnicalAttributes());
        allAttributes.putAll(this.getMentalAttributes().getAllMentalAttributes());
        allAttributes.putAll(this.getPhysicalAttributes().getAllPhysicalAttributes());
        allAttributes.putAll(this.getGoalKeeperAttributes().getAllGoalkeeperAttributes());

        return allAttributes;

    }

    public List<String> getTopNAttributes(int n, Map<String, Integer> allAttributes) {
        List<String> topNAttributes  = allAttributes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();

        return topNAttributes.subList(0, Math.min(n, topNAttributes.size()));
    }

    public void updateName(String name) {
        this.name = name;
    }
}
