package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.player.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlayerDetailsDto {


    public PlayerDetailsDto(Player player, String teamName) {
        this.id = player.getId();
        this.playerApiId = player.getPlayerApiId();
//        this.name = player.getName();
        this.birth = player.getBirth();
        this.age = player.getAge();
        this.height = player.getHeight();
        this.weight = player.getWeight();
        this.teamName = teamName;
        this.nationName = player.getNationName();
        this.nationLogoUrl = player.getNationLogoUrl();
        this.imageUrl = player.getImageUrl();
//
//        // 기술(Technical) 능력치
//        TechnicalAttributes technical = player.getTechnicalAttributes();
//        this.corners = technical.getCorners();
//        this.crossing = technical.getCrossing();
//        this.dribbling = technical.getDribbling();
//        this.finishing = technical.getFinishing();
//        this.firstTouch = technical.getFirstTouch();
//        this.freeKickTaking = technical.getFreeKincks();
//        this.heading = technical.getHeading();
//        this.longShots = technical.getLongShots();
//        this.longThrows = technical.getLongThrows();
//        this.marking = technical.getMarking();
//        this.passing = technical.getPassing();
//        this.penaltyTaking = technical.getPenaltyTaking();
//        this.tackling = technical.getTackling();
//        this.technique = technical.getTechnique();
//
//        // 정신(Mental) 능력치
//        MentalAttributes mental = player.getMentalAttributes();
//        this.aggression = mental.getAggression();
//        this.anticipation = mental.getAnticipation();
//        this.bravery = mental.getBravery();
//        this.composure = mental.getComposure();
//        this.concentration = mental.getConcentration();
//        this.decisions = mental.getDecisions();
//        this.determination = mental.getDetermination();
//        this.flair = mental.getFlair();
//        this.leadership = mental.getLeadership();
//        this.offTheBall = mental.getOffTheBall();
//        this.positioning = mental.getPositioning();
//        this.teamwork = mental.getTeamwork();
//        this.vision = mental.getVision();
//        this.workRate = mental.getWorkRate();
//
//        // 신체(Physical) 능력치
//        PhysicalAttributes physical = player.getPhysicalAttributes();
//        this.acceleration = physical.getAcceleration();
//        this.agility = physical.getAgility();
//        this.balance = physical.getBalance();
//        this.jumpingReach = physical.getJumpingReach();
//        this.naturalFitness = physical.getNaturalFitness();
//        this.pace = physical.getPace();
//        this.stamina = physical.getStamina();
//        this.strength = physical.getStrength();
//
//        // 골키퍼 능력치
//        GoalKeeperAttributes gk = player.getGoalKeeperAttributes();
//        this.aerialAbility = gk.getAerialAbility();
//        this.commandOfArea = gk.getCommandOfArea();
//        this.communication = gk.getCommunication();
//        this.eccentricity = gk.getEccentricity();
//        this.handling = gk.getHandling();
//        this.kicking = gk.getKicking();
//        this.oneOnOnes = gk.getOneOnOnes();
//        this.reflexes = gk.getReflexes();
//        this.rushingOut = gk.getRushingOut();
//        this.tendencyToPunch = gk.getTendencyToPunch();
//        this.throwing = gk.getThrowing();
//
//        // 성격
//        PersonalityAttributes personality = player.getPersonalityAttributes();
//        this.Adaptability = personality.getAdaptability();
//        this.Ambition = personality.getAmbition();
//        this.Loyalty = personality.getLoyalty();
//        this.Pressure = personality.getPressure();
//        this.Professional = personality.getProfessional();
//        this.Sportsmanship = personality.getSportsmanship();
//        this.Temperament = personality.getTemperament();
//        this.Controversy = personality.getControversy();
//
//        // 포지션
//        Position position = player.getPosition();
//        this.Goalkeeper = position.getGoalkeeper();
//        this.DefenderCentral = position.getDefenderCentral();
//        this.DefenderLeft = position.getDefenderLeft();
//        this.DefenderRight = position.getDefenderRight();
//        this.WingBackLeft = position.getWingBackLeft();
//        this.WingBackRight = position.getWingBackRight();
//        this.DefensiveMidfielder = position.getDefensiveMidfielder();
//        this.MidfielderLeft = position.getMidfielderLeft();
//        this.MidfielderRight = position.getMidfielderRight();
//        this.MidfielderCentral = position.getMidfielderCentral();
//        this.AttackingMidCentral = position.getAttackingMidCentral();
//        this.AttackingMidLeft = position.getAttackingMidLeft();
//        this.AttackingMidRight = position.getAttackingMidRight();
//        this.Striker = position.getStriker();
    }

    private Long id;
    private Integer playerApiId;
    private String name;
    private LocalDate birth;
    private int age;
    private int height;
    private int weight;
//    private int marketValue;
    private String teamName;
    private String nationName;
    private String nationLogoUrl;
    private String imageUrl;

//    // 기술(Technical) 능력치
//    private int corners;
//    private int crossing;
//    private int dribbling;
//    private int finishing;
//    private int firstTouch;
//    private int freeKickTaking;
//    private int heading;
//    private int longShots;
//    private int longThrows;
//    private int marking;
//    private int passing;
//    private int penaltyTaking;
//    private int tackling;
//    private int technique;
//
//    // 정신(Mental) 능력치
//    private int aggression;
//    private int anticipation;
//    private int bravery;
//    private int composure;
//    private int concentration;
//    private int decisions;
//    private int determination;
//    private int flair;
//    private int leadership;
//    private int offTheBall;
//    private int positioning;
//    private int teamwork;
//    private int vision;
//    private int workRate;
//
//    // 신체(Physical) 능력치
//    private int acceleration;
//    private int agility;
//    private int balance;
//    private int jumpingReach;
//    private int naturalFitness;
//    private int pace;
//    private int stamina;
//    private int strength;
//
//    // 골키퍼 능력치
//    private int aerialAbility;
//    private int commandOfArea;
//    private int communication;
//    private int eccentricity;
//    private int handling;
//    private int kicking;
//    private int oneOnOnes;
//    private int reflexes;
//    private int rushingOut;
//    private int tendencyToPunch;
//    private int throwing;
//
//    // 성격
//    private int Adaptability;
//    private int Ambition;
//    private int Loyalty;
//    private int Pressure;
//    private int Professional;
//    private int Sportsmanship;
//    private int Temperament;
//    private int Controversy;
//
//    // 포지션
//    private int Goalkeeper;
//    private int DefenderCentral;
//    private int DefenderLeft;
//    private int DefenderRight;
//    private int WingBackLeft;
//    private int WingBackRight;
//    private int DefensiveMidfielder;
//    private int MidfielderLeft;
//    private int MidfielderRight;
//    private int MidfielderCentral;
//    private int AttackingMidCentral;
//    private int AttackingMidLeft;
//    private int AttackingMidRight;
//    private int Striker;
}
