package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.player.*;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FmPlayerDetailsDto {
    private String name;
    private Position position;
    private FmVersion version;
    private Integer fmUid;
    private int currentAbility;
    private int potentialAbility;
    private PersonalityAttributes personalityAttributes;
    private TechnicalAttributes technicalAttributes;
    private MentalAttributes mentalAttributes;
    private PhysicalAttributes physicalAttributes;
    private GoalKeeperAttributes goalKeeperAttributes;
    private HiddenAttributes hiddenAttributes;
    
    public FmPlayerDetailsDto(FmPlayer fmPlayer) {
        this.name = fmPlayer.getName();
        this.position = fmPlayer.getPosition();
        this.version = fmPlayer.getFmVersion();
        this.fmUid = fmPlayer.getFmUid();
        this.currentAbility = fmPlayer.getCurrentAbility();
        this.potentialAbility = fmPlayer.getPotentialAbility();
        this.personalityAttributes = fmPlayer.getPersonalityAttributes();
        this.technicalAttributes = fmPlayer.getTechnicalAttributes();
        this.mentalAttributes = fmPlayer.getMentalAttributes();
        this.physicalAttributes = fmPlayer.getPhysicalAttributes();
        this.goalKeeperAttributes = fmPlayer.getGoalKeeperAttributes();
        this.hiddenAttributes = fmPlayer.getHiddenAttributes();
    }
}
