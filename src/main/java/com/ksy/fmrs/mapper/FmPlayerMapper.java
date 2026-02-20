package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.GoalKeeperAttributes;
import com.ksy.fmrs.domain.player.HiddenAttributes;
import com.ksy.fmrs.domain.player.MentalAttributes;
import com.ksy.fmrs.domain.player.PersonalityAttributes;
import com.ksy.fmrs.domain.player.PhysicalAttributes;
import com.ksy.fmrs.domain.player.Position;
import com.ksy.fmrs.domain.player.TechnicalAttributes;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.util.NationNormalizer;
import com.ksy.fmrs.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FmPlayerMapper {

    public List<FmPlayer> toEntity(List<FmPlayerDto> fmPlayerDtos, FmVersion fmVersion) {
        List<FmPlayer> fmPlayers = new ArrayList<>();
        fmPlayerDtos.forEach(fmPlayer -> fmPlayers.add(toEntity(fmPlayer, fmVersion)));

        return fmPlayers;
    }

    public FmPlayer toEntity(FmPlayerDto fmPlayerDto, FmVersion fmVersion) {
        String name = fmPlayerDto.getName();
        return FmPlayer.builder()
                .name(name)
                .fmVersion(fmVersion)
                .fmUid(fmPlayerDto.getFmPlayerId())
                .firstName(StringUtils.getFirstName(name).toUpperCase())
                .lastName(StringUtils.getLastName(name).toUpperCase())
                .birth(fmPlayerDto.getBorn())
                .nationName(NationNormalizer.normalize(fmPlayerDto.getNation().getName().toUpperCase()))
                .personalityAttributes(toPersonalityAttributes(fmPlayerDto))
                .hiddenAttributes(toHiddenAttributes(fmPlayerDto))
                .mentalAttributes(toMentalAttributes(fmPlayerDto))
                .physicalAttributes(toPhysicalAttributes(fmPlayerDto))
                .goalKeeperAttributes(toGoalKeeperAttributes(fmPlayerDto))
                .technicalAttributes(toTechnicalAttributes(fmPlayerDto))
                .position(toPosition(fmPlayerDto))
                .currentAbility(fmPlayerDto.getCurrentAbility())
                .potentialAbility(fmPlayerDto.getPotentialAbility())
                .build();
    }

    private GoalKeeperAttributes toGoalKeeperAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.GoalKeeperAttributesDto goalKeeperAttributesDto = fmPlayerDto.getGoalKeeperAttributes();
        if (goalKeeperAttributesDto == null) {
            return null;
        }
        return GoalKeeperAttributes.builder()
                .aerialAbility(goalKeeperAttributesDto.getAerialAbility())
                .commandOfArea(goalKeeperAttributesDto.getCommandOfArea())
                .communication(goalKeeperAttributesDto.getCommunication())
                .eccentricity(goalKeeperAttributesDto.getEccentricity())
                .handling(goalKeeperAttributesDto.getHandling())
                .kicking(goalKeeperAttributesDto.getKicking())
                .oneOnOnes(goalKeeperAttributesDto.getOneOnOnes())
                .reflexes(goalKeeperAttributesDto.getReflexes())
                .rushingOut(goalKeeperAttributesDto.getRushingOut())
                .tendencyToPunch(goalKeeperAttributesDto.getTendencyToPunch())
                .throwing(goalKeeperAttributesDto.getThrowing())
                .build();
    }

    private HiddenAttributes toHiddenAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.HiddenAttributesDto hiddenAttributesDto = fmPlayerDto.getHiddenAttributes();
        if (hiddenAttributesDto == null) {
            return null;
        }
        return HiddenAttributes.builder()
                .consistency(hiddenAttributesDto.getConsistency())
                .dirtiness(hiddenAttributesDto.getDirtiness())
                .importantMatches(hiddenAttributesDto.getImportantMatches())
                .injuryProneness(hiddenAttributesDto.getInjuryProness())
                .versatility(hiddenAttributesDto.getVersatility())
                .build();
    }

    private MentalAttributes toMentalAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.MentalAttributesDto mentalAttributesDto = fmPlayerDto.getMentalAttributes();
        if (mentalAttributesDto == null) {
            return null;
        }
        return MentalAttributes.builder()
                .aggression(mentalAttributesDto.getAggression())
                .anticipation(mentalAttributesDto.getAnticipation())
                .bravery(mentalAttributesDto.getBravery())
                .composure(mentalAttributesDto.getComposure())
                .concentration(mentalAttributesDto.getConcentration())
                .decisions(mentalAttributesDto.getDecisions())
                .determination(mentalAttributesDto.getDetermination())
                .flair(mentalAttributesDto.getFlair())
                .leadership(mentalAttributesDto.getLeadership())
                .offTheBall(mentalAttributesDto.getOffTheBall())
                .positioning(mentalAttributesDto.getPositioning())
                .teamwork(mentalAttributesDto.getTeamwork())
                .vision(mentalAttributesDto.getVision())
                .workRate(mentalAttributesDto.getWorkrate())
                .build();
    }

    private PersonalityAttributes toPersonalityAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PersonalityAttributesDto personalityAttributesDto = fmPlayerDto.getPersonalityAttributes();
        if (personalityAttributesDto == null) {
            return null;
        }
        return PersonalityAttributes.builder()
                .adaptability(personalityAttributesDto.getAdaptability())
                .ambition(personalityAttributesDto.getAmbition())
                .loyalty(personalityAttributesDto.getLoyalty())
                .pressure(personalityAttributesDto.getPressure())
                .professional(personalityAttributesDto.getProfessional())
                .sportsmanship(personalityAttributesDto.getSportsmanship())
                .temperament(personalityAttributesDto.getTemperament())
                .controversy(personalityAttributesDto.getControversy())
                .build();
    }

    private PhysicalAttributes toPhysicalAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PhysicalAttributesDto physicalAttributesDto = fmPlayerDto.getPhysicalAttributes();
        if (physicalAttributesDto == null) {
            return null;
        }
        return PhysicalAttributes.builder()
                .acceleration(physicalAttributesDto.getAcceleration())
                .agility(physicalAttributesDto.getAgility())
                .balance(physicalAttributesDto.getBalance())
                .jumpingReach(physicalAttributesDto.getJumping())
                .naturalFitness(physicalAttributesDto.getNaturalFitness())
                .pace(physicalAttributesDto.getPace())
                .stamina(physicalAttributesDto.getStamina())
                .strength(physicalAttributesDto.getStrength())
                .build();
    }

    private TechnicalAttributes toTechnicalAttributes(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.TechnicalAttributesDto technicalAttributesDto = fmPlayerDto.getTechnicalAttributes();
        if (technicalAttributesDto == null) {
            return null;
        }
        return TechnicalAttributes.builder()
                .corners(technicalAttributesDto.getCorners())
                .crossing(technicalAttributesDto.getCrossing())
                .dribbling(technicalAttributesDto.getDribbling())
                .finishing(technicalAttributesDto.getFinishing())
                .firstTouch(technicalAttributesDto.getFirstTouch())
                .freeKicks(technicalAttributesDto.getFreekicks())
                .heading(technicalAttributesDto.getHeading())
                .longShots(technicalAttributesDto.getLongShots())
                .longThrows(technicalAttributesDto.getLongthrows())
                .marking(technicalAttributesDto.getMarking())
                .passing(technicalAttributesDto.getPassing())
                .penaltyTaking(technicalAttributesDto.getPenaltyTaking())
                .tackling(technicalAttributesDto.getTackling())
                .technique(technicalAttributesDto.getTechnique())
                .build();
    }

    private Position toPosition(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PositionAttributesDto positionAttributesDto = fmPlayerDto.getPositions();
        if (positionAttributesDto == null) {
            return null;
        }
        return Position.builder()
                .goalkeeper(positionAttributesDto.getGoalkeeper())
                .defenderCentral(positionAttributesDto.getDefenderCentral())
                .defenderLeft(positionAttributesDto.getDefenderLeft())
                .defenderRight(positionAttributesDto.getDefenderRight())
                .wingBackLeft(positionAttributesDto.getWingBackLeft())
                .wingBackRight(positionAttributesDto.getWingBackRight())
                .defensiveMidfielder(positionAttributesDto.getDefensiveMidfielder())
                .midfielderCentral(positionAttributesDto.getMidfielderCentral())
                .midfielderLeft(positionAttributesDto.getMidfielderLeft())
                .midfielderRight(positionAttributesDto.getMidfielderRight())
                .attackingMidCentral(positionAttributesDto.getAttackingMidCentral())
                .attackingMidLeft(positionAttributesDto.getAttackingMidLeft())
                .attackingMidRight(positionAttributesDto.getAttackingMidRight())
                .striker(positionAttributesDto.getStriker())
                .build();
    }
}
