package com.ksy.fmrs.dto.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FmPlayerDto {
    // 기본 속성
    private String name;

    private Integer fmUid;

    @JsonProperty("CA")
    private int currentAbility;

    @JsonProperty("PA")
    private int potentialAbility;

    @JsonProperty("AskingPrice")
    private int askingPrice;

    @JsonProperty("MatchSharpness")
    private int matchSharpness;

    @JsonProperty("Condition")
    private int condition;

    @JsonProperty("Jadedness")
    private int jadedness;

    @JsonProperty("Height")
    private int height;

    @JsonProperty("Weight")
    private int weight;

    @JsonProperty("Morale")
    private int morale;

    @JsonProperty("WorldReputation")
    private int worldReputation;

    @JsonProperty("CurrentReputation")
    private int currentReputation;

    @JsonProperty("HomeReputation")
    private int homeReputation;

    // 중첩 객체들
    @JsonProperty("GoalKeeperAttributes")
    private GoalKeeperAttributesDto goalKeeperAttributes;

    @JsonProperty("MentalAttributes")
    private MentalAttributesDto mentalAttributes;

    @JsonProperty("PhysicalAttributes")
    private PhysicalAttributesDto physicalAttributes;

    @JsonProperty("HiddenAttributes")
    private HiddenAttributesDto hiddenAttributes;

    @JsonProperty("TechnicalAttributes")
    private TechnicalAttributesDto technicalAttributes;

    @JsonProperty("Positions")
    private PositionAttributesDto positions;

    @JsonProperty("PreferredMoves")
    private long preferredMoves;

    @JsonProperty("Number")
    private int number;

    // 플레이어 ID (FM에서 제공하는 값)
    @JsonProperty("Id")
    private int fmPlayerId;

    @JsonProperty("Ethnicity")
    private String ethnicity;

    @JsonProperty("SkinColour")
    private int skinColour;

    @JsonProperty("HairColour")
    private String hairColour;

    @JsonProperty("HairLength")
    private String hairLength;

    // ISO 8601 형식으로 들어오는 출생일 (예: "1989-06-10T00:00:00")
    @JsonProperty("Born")
    private LocalDate born;

    @JsonProperty("Nation")
    private NationDto nation;

    @JsonProperty("IntCaps")
    private int intCaps;

    @JsonProperty("IntGoals")
    private int intGoals;

    @JsonProperty("U21Caps")
    private int u21Caps;

    @JsonProperty("U21Goals")
    private int u21Goals;

    @JsonProperty("PersonalityAttributes")
    private PersonalityAttributesDto personalityAttributes;

    @JsonProperty("DocumentType")
    private String documentType;

    @JsonProperty("FmrteVersion")
    private String fmrteVersion;

    @JsonProperty("RCA")
    private int rca;

    @JsonProperty("ActualRating")
    private double actualRating;

    @JsonProperty("PotentialRating")
    private double potentialRating;

    // Nested DTO 클래스들

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalKeeperAttributesDto {
        @JsonProperty("AerialAbility")
        private int aerialAbility;
        @JsonProperty("CommandOfArea")
        private int commandOfArea;
        @JsonProperty("Communication")
        private int communication;
        @JsonProperty("Eccentricity")
        private int eccentricity;
        @JsonProperty("Handling")
        private int handling;
        @JsonProperty("Kicking")
        private int kicking;
        @JsonProperty("OneOnOnes")
        private int oneOnOnes;
        @JsonProperty("Reflexes")
        private int reflexes;
        @JsonProperty("RushingOut")
        private int rushingOut;
        @JsonProperty("TendencyToPunch")
        private int tendencyToPunch;
        @JsonProperty("Throwing")
        private int throwing;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MentalAttributesDto {
        @JsonProperty("Aggression")
        private int aggression;
        @JsonProperty("Anticipation")
        private int anticipation;
        @JsonProperty("Bravery")
        private int bravery;
        @JsonProperty("Composure")
        private int composure;
        @JsonProperty("Concentration")
        private int concentration;
        @JsonProperty("Vision")
        private int vision;
        @JsonProperty("Decisions")
        private int decisions;
        @JsonProperty("Determination")
        private int determination;
        @JsonProperty("Flair")
        private int flair;
        @JsonProperty("Leadership")
        private int leadership;
        @JsonProperty("OffTheBall")
        private int offTheBall;
        @JsonProperty("Positioning")
        private int positioning;
        @JsonProperty("Teamwork")
        private int teamwork;
        @JsonProperty("Workrate")
        private int workrate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhysicalAttributesDto {
        @JsonProperty("Acceleration")
        private int acceleration;
        @JsonProperty("Agility")
        private int agility;
        @JsonProperty("Balance")
        private int balance;
        @JsonProperty("Jumping")
        private int jumping;
        @JsonProperty("LeftFoot")
        private int leftFoot;
        @JsonProperty("NaturalFitness")
        private int naturalFitness;
        @JsonProperty("Pace")
        private int pace;
        @JsonProperty("RightFoot")
        private int rightFoot;
        @JsonProperty("Stamina")
        private int stamina;
        @JsonProperty("Strength")
        private int strength;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HiddenAttributesDto {
        @JsonProperty("Consistency")
        private int consistency;
        @JsonProperty("Dirtiness")
        private int dirtiness;
        @JsonProperty("ImportantMatches")
        private int importantMatches;
        @JsonProperty("InjuryProness")
        private int injuryProness;
        @JsonProperty("Versatility")
        private int versatility;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TechnicalAttributesDto {
        @JsonProperty("Corners")
        private int corners;
        @JsonProperty("Crossing")
        private int crossing;
        @JsonProperty("Dribbling")
        private int dribbling;
        @JsonProperty("Finishing")
        private int finishing;
        @JsonProperty("FirstTouch")
        private int firstTouch;
        @JsonProperty("Freekicks")
        private int freekicks;
        @JsonProperty("Heading")
        private int heading;
        @JsonProperty("LongShots")
        private int longShots;
        @JsonProperty("Longthrows")
        private int longthrows;
        @JsonProperty("Marking")
        private int marking;
        @JsonProperty("Passing")
        private int passing;
        @JsonProperty("PenaltyTaking")
        private int penaltyTaking;
        @JsonProperty("Tackling")
        private int tackling;
        @JsonProperty("Technique")
        private int technique;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionAttributesDto {
        @JsonProperty("Goalkeeper")
        private int goalkeeper;
        @JsonProperty("Striker")
        private int striker;
        @JsonProperty("AttackingMidCentral")
        private int attackingMidCentral;
        @JsonProperty("AttackingMidLeft")
        private int attackingMidLeft;
        @JsonProperty("AttackingMidRight")
        private int attackingMidRight;
        @JsonProperty("DefenderCentral")
        private int defenderCentral;
        @JsonProperty("DefenderLeft")
        private int defenderLeft;
        @JsonProperty("DefenderRight")
        private int defenderRight;
        @JsonProperty("DefensiveMidfielder")
        private int defensiveMidfielder;
        @JsonProperty("MidfielderCentral")
        private int midfielderCentral;
        @JsonProperty("MidfielderLeft")
        private int midfielderLeft;
        @JsonProperty("MidfielderRight")
        private int midfielderRight;
        @JsonProperty("WingBackLeft")
        private int wingBackLeft;
        @JsonProperty("WingBackRight")
        private int wingBackRight;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalityAttributesDto {
        @JsonProperty("Adaptability")
        private int adaptability;
        @JsonProperty("Ambition")
        private int ambition;
        @JsonProperty("Loyalty")
        private int loyalty;
        @JsonProperty("Pressure")
        private int pressure;
        @JsonProperty("Professional")
        private int professional;
        @JsonProperty("Sportsmanship")
        private int sportsmanship;
        @JsonProperty("Temperament")
        private int temperament;
        @JsonProperty("Controversy")
        private int controversy;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NationDto {
        @JsonProperty("FIFAPosition")
        private int fifaPosition;
        @JsonProperty("FIFAScore")
        private int fifaScore;
        @JsonProperty("Id")
        private int nationApiId;
        @JsonProperty("Name")
        private String name;
        @JsonProperty("ShortName")
        private String shortName;
        @JsonProperty("Reputation")
        private int reputation;
        @JsonProperty("FirstTeamManager")
        private String firstTeamManager;
    }
}