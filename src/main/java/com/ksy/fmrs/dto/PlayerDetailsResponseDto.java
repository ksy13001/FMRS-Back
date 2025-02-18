package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.enums.PositionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlayerDetailsResponseDto {

    public PlayerDetailsResponseDto(Player player, String teamName, String nationName) {
        this.id = player.getId();
        this.name = player.getName();
        this.birth = player.getBirth();
        this.age = player.getAge();
        this.height = player.getHeight();
        this.weight = player.getWeight();
        this.marketValue = player.getMarketValue();
        this.position = player.getPosition();
        this.teamName = teamName;
        this.nationName = nationName;
        this.imageUrl = player.getImageUrl();

        this.corners = player.getCorners();
        this.crossing = player.getCrossing();
        this.dribbling = player.getDribbling();
        this.finishing = player.getFinishing();
        this.firstTouch = player.getFirstTouch();
        this.freeKickTaking = player.getFreeKickTaking();
        this.heading = player.getHeading();
        this.longShots = player.getLongShots();
        this.longThrows = player.getLongThrows();
        this.marking = player.getMarking();
        this.passing = player.getPassing();
        this.penaltyTaking = player.getPenaltyTaking();
        this.tackling = player.getTackling();
        this.technique = player.getTechnique();

        this.aggression = player.getAggression();
        this.anticipation = player.getAnticipation();
        this.bravery = player.getBravery();
        this.composure = player.getComposure();
        this.concentration = player.getConcentration();
        this.decisions = player.getDecisions();
        this.determination = player.getDetermination();
        this.flair = player.getFlair();
        this.leadership = player.getLeadership();
        this.offTheBall = player.getOffTheBall();
        this.positioning = player.getPositioning();
        this.teamwork = player.getTeamwork();
        this.vision = player.getVision();
        this.workRate = player.getWorkRate();

        this.acceleration = player.getAcceleration();
        this.agility = player.getAgility();
        this.balance = player.getBalance();
        this.jumpingReach = player.getJumpingReach();
        this.naturalFitness = player.getNaturalFitness();
        this.pace = player.getPace();
        this.stamina = player.getStamina();
        this.strength = player.getStrength();
    }
    private Long id;
    private String name;
    private LocalDate birth;
    private int age;
    private int height;
    private int weight;
    private int marketValue;
    private PositionEnum position;
    private String teamName;
    private String nationName;
    private String imageUrl;

    private int corners;
    private int crossing;
    private int dribbling;
    private int finishing;
    private int firstTouch;
    private int freeKickTaking;
    private int heading;
    private int longShots;
    private int longThrows;
    private int marking;
    private int passing;
    private int penaltyTaking;
    private int tackling;
    private int technique;

    // 정신(Mental) 능력치
    private int aggression;
    private int anticipation;
    private int bravery;
    private int composure;
    private int concentration;
    private int decisions;
    private int determination;
    private int flair;
    private int leadership;
    private int offTheBall;
    private int positioning;
    private int teamwork;
    private int vision;
    private int workRate;

    // 신체(Physical) 능력치
    private int acceleration;
    private int agility;
    private int balance;
    private int jumpingReach;
    private int naturalFitness;
    private int pace;
    private int stamina;
    private int strength;
}
