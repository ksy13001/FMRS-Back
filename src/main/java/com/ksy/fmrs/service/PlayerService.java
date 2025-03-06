package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.search.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;

    /**
     * 선수 상세 정보 조회
     * */
    public PlayerDetailsDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertPlayerToPlayerDetailsResponseDto(player);
    }

    private PlayerDetailsDto convertPlayerToPlayerDetailsResponseDto(Player player) {
        return new PlayerDetailsDto(player,  getTeamNameByPlayer(player));
    }

    private String getTeamNameByPlayer(Player player) {
        return Optional.ofNullable(player.getTeam())
                .map(Team::getName)
                .orElse(null);
    }

    @Transactional
    public void savePlayersFromFmPlayers(String dirPath){
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            log.warn("No JSON files found in folder: {}", dirPath);
            return;
        }
        List<Player> players = new ArrayList<>();
        for (File file : jsonFiles) {
            try {
                // DB에 저장
                players.add(importPlayerFromJson(file));
                log.info("Imported player from file: {}", file.getName());
            } catch (Exception e) {
                log.error("Error importing file {}: {}", file.getName(), e.getMessage(), e);
            }
        }
        playerRepository.saveAll(players);
    }

    private Player importPlayerFromJson(File file) throws IOException {
        FmPlayerDto fmPlayerDto = objectMapper.readValue(file, FmPlayerDto.class);
        return convertFmPlayerDtoToPlayer(getPlayerNameFromFileName(file.getName()), fmPlayerDto);
    }


    /**
     * 팀 소속 선수들 모두 조회
     * */
    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 모든 선수 몸값순 조회
     * */
    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }


    /**
     * 선수 이름 검색
     * */
    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 선수 상세 조회
     * */
    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    private Player convertFmPlayerDtoToPlayer(String name, FmPlayerDto fmPlayerDto) {
        return Player.builder()
                .name(name)
                .age(convertBirthToAge(fmPlayerDto.getBorn()))
                .birth(fmPlayerDto.getBorn())
                .height(fmPlayerDto.getHeight())
                .weight(fmPlayerDto.getWeight())
                .marketValue(fmPlayerDto.getAskingPrice())
                .currentAbility(fmPlayerDto.getCurrentAbility())
                .potentialAbility(fmPlayerDto.getPotentialAbility())
                .goalKeeperAttributes(getGoalKeeperAttributesFromFmPlayer(fmPlayerDto))
                .hiddenAttributes(getHiddenAttributesFromFmPlayer(fmPlayerDto))
                .mentalAttributes(getMentalAttributesFromFmPlayer(fmPlayerDto))
                .personalityAttributes(getPersonalityAttributesFromFmPlayer(fmPlayerDto))
                .physicalAttributes(getPhysicalAttributesFromFmPlayer(fmPlayerDto))
                .technicalAttributes(getTechnicalAttributesFromFmPlayer(fmPlayerDto))
                .position(getPositionFromFmPlayer(fmPlayerDto))
                .build();
    }

    private int convertBirthToAge(LocalDate birth) {
        if (birth == null) {
            throw new IllegalArgumentException("birth is null");
        }
        return Period.between(birth, LocalDate.now()).getYears();
    }

    //103607-James Henry
    private String getPlayerNameFromFileName(String fileName) {
        if(fileName == null) {
            throw new IllegalArgumentException("fileName is null");
        }
        // 확장자(.json) 제거
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            fileName = fileName.substring(0, dotIndex);
        }
        // 첫 번째 하이픈 위치 찾기
        int hyphenIndex = fileName.indexOf('-');
        if (hyphenIndex == -1 || hyphenIndex == fileName.length() - 1) {
            // 하이픈이 없거나 하이픈이 마지막이면 전체 문자열 반환
            return fileName.trim();
        }
        // 첫 번째 하이픈 이후의 모든 문자열을 이름으로 사용 (이름에 하이픈이 포함될 수 있음)
        return fileName.substring(hyphenIndex + 1).trim();
    }


    private GoalKeeperAttributes getGoalKeeperAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.GoalKeeperAttributesDto goalKeeperAttributesDto = fmPlayerDto.getGoalKeeperAttributes();
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

    private HiddenAttributes getHiddenAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.HiddenAttributesDto hiddenAttributesDto = fmPlayerDto.getHiddenAttributes();
        return HiddenAttributes.builder()
                .consistency(hiddenAttributesDto.getConsistency())
                .dirtiness(hiddenAttributesDto.getDirtiness())
                .importantMatches(hiddenAttributesDto.getImportantMatches())
                .injuryProneness(hiddenAttributesDto.getInjuryProness())
                .versatility(hiddenAttributesDto.getVersatility())
                .build();
    }

    private MentalAttributes getMentalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.MentalAttributesDto mentalAttributesDto = fmPlayerDto.getMentalAttributes();
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
                .build();
    }

    private PersonalityAttributes getPersonalityAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PersonalityAttributesDto personalityAttributesDto = fmPlayerDto.getPersonalityAttributes();
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

    private PhysicalAttributes getPhysicalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PhysicalAttributesDto physicalAttributesDto = fmPlayerDto.getPhysicalAttributes();
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

    private TechnicalAttributes getTechnicalAttributesFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.TechnicalAttributesDto technicalAttributesDto = fmPlayerDto.getTechnicalAttributes();
        return TechnicalAttributes.builder()
                .corners(technicalAttributesDto.getCorners())
                .crossing(technicalAttributesDto.getCrossing())
                .dribbling(technicalAttributesDto.getDribbling())
                .finishing(technicalAttributesDto.getFinishing())
                .firstTouch(technicalAttributesDto.getFirstTouch())
                .freeKincks(technicalAttributesDto.getFreekicks())
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

    private Position getPositionFromFmPlayer(FmPlayerDto fmPlayerDto) {
        FmPlayerDto.PositionAttributesDto positionAttributesDto = fmPlayerDto.getPositions();
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
