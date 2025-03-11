package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.league.LeagueStandingDto;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.dto.search.TeamDetailsDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.util.StringUtils;
import com.ksy.fmrs.util.TimeUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitializationService {
    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final FootballApiService footballApiService;
    private final LeagueService leagueService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final EntityManager em;
    private static final int Last_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;
    private static final int SEASON_2024 = 2024;
    private static final int default_page = 1;


    /**
     * 1. 외부 api로 전체 리그 정보 다 가져옴
     * 2. 외부 api로 리그마다 standing 가져와서 팀 정보 다 가져옴
     * 3. 외부 api로 팀 마다 statisitcs 가져와서 선수 정보 다 가져옴(player_api_id 매핑하기 위함)
     * 4. bulk insert
     * **/
    public void createInitialData(){
        // 리그 데이터 전부 저장
        List<LeagueStandingDto> leagueStandingDtos = createInitialLeague();
        // 팀 데이터 전부 저장
        saveInitialTeams(leagueStandingDtos);
        // 선수 매핑
    }

    private List<LeagueStandingDto> createInitialLeague() {
        List<LeagueDetailsRequestDto>  leagues = new ArrayList<>();
        List<LeagueStandingDto> leagueStandingDtos = new ArrayList<>();
        for(int nowLeagueApiId = FIRST_LEAGUE_ID; nowLeagueApiId <= Last_LEAGUE_ID; nowLeagueApiId++){
            LeagueDetailsRequestDto leagueDetailsRequestDto = footballApiService.getLeagueInfo(nowLeagueApiId);
            if(!isLeagueType(leagueDetailsRequestDto)){
                continue;
            }
            LeagueStandingDto leagueStandingDto = getLeagueStandingDto(nowLeagueApiId, leagueDetailsRequestDto.getCurrentSeason());
            if(leagueStandingDto == null){
                continue;
            }
            if(leagueRepository.findLeagueByLeagueApiId(leagueDetailsRequestDto.getLeagueApiId()).isEmpty()){
                leagues.add(leagueDetailsRequestDto);
            }
            leagueStandingDtos.add(leagueStandingDto);
        }
        saveInitialLeagues(leagues);
        return leagueStandingDtos;
    }

    private void saveInitialTeams(List<LeagueStandingDto> leagueStandingDtos) {
        List<TeamDetailsDto> teams = new ArrayList<>();
        List<PlayerStatisticsApiResponseDto>  playerStatistics = new ArrayList<>();
        leagueStandingDtos.forEach(leagueStandingDto -> {
            leagueStandingDto.getStandings().forEach(standingDto -> {
                TeamDetailsDto teamDetailsDto = footballApiService.getTeamDetails(
                        leagueStandingDto.getLeagueApiId(),
                        standingDto.getTeamApiId(),
                        leagueStandingDto.getCurrentSeason());;
                teams.add(teamDetailsDto);
            });
        });
        teamService.saveAllByTeamDetails(teams);
    }

//


    private LeagueStandingDto getLeagueStandingDto(Integer nowLeagueApiId, int currentSeason){
        LeagueStandingDto leagueStandingDto  = footballApiService.getLeagueStandings(nowLeagueApiId, currentSeason);
        if(leagueStandingDto.getStandings().isEmpty()){
            // leagueApiId = 447 인 경우 leagueInfo 에서 standing=true 인데 실제 standing 요청시 null 인경우 존재
            return null;
        }
        return leagueStandingDto;
    }

    private Boolean isLeagueType(LeagueDetailsRequestDto leagueDetailsRequestDto) {
        return leagueDetailsRequestDto != null &&
                leagueDetailsRequestDto.getLeagueType().equals(LeagueType.LEAGUE.getValue()) &&
                leagueDetailsRequestDto.getCurrentSeason() >= SEASON_2024 &&
                leagueDetailsRequestDto.getStanding();
    }

    private void saveInitialLeagues(List<LeagueDetailsRequestDto> leagueDetailsRequestDto) {
        leagueService.saveAllByLeagueDetails(leagueDetailsRequestDto);
    }

//    private void saveInitialTeam(Integer leagueId, Integer teamId, Integer currentSeason) {
//        if(teamRepository.findTeamByTeamApiId(teamId).isEmpty()){
//            TeamDetailsDto teamDetailsDto = footballApiService.getTeamDetails(leagueId, teamId, currentSeason);
//            teamService.saveAllByTeamDetails(teamDetailsDto);
//        }
//    }

    @Transactional
    public void savePlayersFromFmPlayers(String dirPath){
        playerRepository.saveAll(getPlayersFromFmPlayers(dirPath));
    }

    public List<Player> getPlayersFromFmPlayers(String dirPath) {
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        List<Player> players = new ArrayList<>();
        Arrays.stream(jsonFiles).forEach(file -> {
            try {
                // DB에 저장
                players.add(importPlayerFromJson(file));
            } catch (Exception e) {
                log.error("Error importing file {}: {}", file.getName(), e.getMessage(), e);
            }
        });
        return players;
    }

    private Player importPlayerFromJson(File file) throws IOException {
        FmPlayerDto fmPlayerDto = objectMapper.readValue(file, FmPlayerDto.class);
        return convertFmPlayerDtoToPlayer(StringUtils.getPlayerNameFromFileName(file.getName()), fmPlayerDto);
    }

    private Player convertFmPlayerDtoToPlayer(String name, FmPlayerDto fmPlayerDto) {
        return Player.builder()
                .name(name)
                .firstName(StringUtils.getFirstName(name))
                .lastName(StringUtils.getLastName(name))
                .age(TimeUtils.getAge(fmPlayerDto.getBorn()))
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
