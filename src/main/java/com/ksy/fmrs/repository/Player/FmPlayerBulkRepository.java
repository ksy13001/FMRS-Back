package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FmPlayerBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertFmPlayersInBatches(List<FmPlayer> fmPlayers, int batch){
        int total = fmPlayers.size();

        for (int i = 0; i < total; i += batch) {
            int end = Math.min(i + batch, total);
            List<FmPlayer> now = new ArrayList<>(fmPlayers.subList(i, end));
            bulkInsertFmPlayer(now);
        }
    }


    public void bulkInsertFmPlayer(List<FmPlayer> fmPlayers) {
        log.info("전달받은 fmplayer 개수:{}", fmPlayers.size());

        List<String> columns = List.of(
                "name", "current_ability", "potential_ability",
                "adaptability", "ambition", "loyalty", "pressure", "professional", "sportsmanship", "temperament", "controversy",
                "corners", "crossing", "dribbling", "finishing", "first_touch", "free_kick_taking", "heading",
                "long_shots", "long_throws", "marking", "passing", "penalty_taking", "tackling", "technique",
                "aggression", "anticipation", "bravery", "composure", "concentration", "decisions", "determination",
                "flair", "leadership", "off_the_ball", "positioning", "teamwork", "vision", "work_rate",
                "acceleration", "agility", "balance", "jumping_reach", "natural_fitness", "pace", "stamina", "strength",
                "aerial_ability", "command_of_area", "communication", "eccentricity", "handling", "kicking", "one_on_ones",
                "reflexes", "rushing_out", "tendency_to_punch", "throwing",
                "consistency", "dirtiness", "important_matches", "injury_proneness", "versatility",
                "goalkeeper", "defender_central", "defender_left", "defender_right", "wing_back_left", "wing_back_right",
                "defensive_midfielder", "midfielder_left", "midfielder_right", "midfielder_central",
                "attacking_mid_central", "attacking_mid_left", "attacking_mid_right", "striker",
                "first_name", "last_name", "birth", "nation_name", "fm_uid", "fm_version"
        );
        String sql = SqlUtils.buildInsertSql("fmplayer", columns);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FmPlayer fmPlayer = fmPlayers.get(i);
                ps.setString(1, fmPlayer.getName());
                ps.setInt(2, fmPlayer.getCurrentAbility());
                ps.setInt(3, fmPlayer.getPotentialAbility());
                ps.setInt(4, fmPlayer.getPersonalityAttributes().getAdaptability());
                ps.setInt(5, fmPlayer.getPersonalityAttributes().getAmbition());
                ps.setInt(6, fmPlayer.getPersonalityAttributes().getLoyalty());
                ps.setInt(7, fmPlayer.getPersonalityAttributes().getPressure());
                ps.setInt(8, fmPlayer.getPersonalityAttributes().getProfessional());
                ps.setInt(9, fmPlayer.getPersonalityAttributes().getSportsmanship());
                ps.setInt(10, fmPlayer.getPersonalityAttributes().getTemperament());
                ps.setInt(11, fmPlayer.getPersonalityAttributes().getControversy());
                ps.setInt(12, fmPlayer.getTechnicalAttributes().getCorners());
                ps.setInt(13, fmPlayer.getTechnicalAttributes().getCrossing());
                ps.setInt(14, fmPlayer.getTechnicalAttributes().getDribbling());
                ps.setInt(15, fmPlayer.getTechnicalAttributes().getFinishing());
                ps.setInt(16, fmPlayer.getTechnicalAttributes().getFirstTouch());ps.setInt(17, fmPlayer.getTechnicalAttributes().getFreeKicks());
                ps.setInt(18, fmPlayer.getTechnicalAttributes().getHeading());
                ps.setInt(19, fmPlayer.getTechnicalAttributes().getLongShots());
                ps.setInt(20, fmPlayer.getTechnicalAttributes().getLongThrows());
                ps.setInt(21, fmPlayer.getTechnicalAttributes().getMarking());
                ps.setInt(22, fmPlayer.getTechnicalAttributes().getPassing());
                ps.setInt(23, fmPlayer.getTechnicalAttributes().getPenaltyTaking());
                ps.setInt(24, fmPlayer.getTechnicalAttributes().getTackling());
                ps.setInt(25, fmPlayer.getTechnicalAttributes().getTechnique());
                ps.setInt(26, fmPlayer.getMentalAttributes().getAggression());
                ps.setInt(27, fmPlayer.getMentalAttributes().getAnticipation());
                ps.setInt(28, fmPlayer.getMentalAttributes().getBravery());
                ps.setInt(29, fmPlayer.getMentalAttributes().getComposure());
                ps.setInt(30, fmPlayer.getMentalAttributes().getConcentration());
                ps.setInt(31, fmPlayer.getMentalAttributes().getDecisions());
                ps.setInt(32, fmPlayer.getMentalAttributes().getDetermination());
                ps.setInt(33, fmPlayer.getMentalAttributes().getFlair());
                ps.setInt(34, fmPlayer.getMentalAttributes().getLeadership());
                ps.setInt(35, fmPlayer.getMentalAttributes().getOffTheBall());
                ps.setInt(36, fmPlayer.getMentalAttributes().getPositioning());
                ps.setInt(37, fmPlayer.getMentalAttributes().getTeamwork());
                ps.setInt(38, fmPlayer.getMentalAttributes().getVision());
                ps.setInt(39, fmPlayer.getMentalAttributes().getWorkRate());
                ps.setInt(40, fmPlayer.getPhysicalAttributes().getAcceleration());
                ps.setInt(41, fmPlayer.getPhysicalAttributes().getAgility());
                ps.setInt(42, fmPlayer.getPhysicalAttributes().getBalance());
                ps.setInt(43, fmPlayer.getPhysicalAttributes().getJumpingReach());
                ps.setInt(44, fmPlayer.getPhysicalAttributes().getNaturalFitness());
                ps.setInt(45, fmPlayer.getPhysicalAttributes().getPace());
                ps.setInt(46, fmPlayer.getPhysicalAttributes().getStamina());
                ps.setInt(47, fmPlayer.getPhysicalAttributes().getStrength());
                ps.setInt(48, fmPlayer.getGoalKeeperAttributes().getAerialAbility());
                ps.setInt(49, fmPlayer.getGoalKeeperAttributes().getCommandOfArea());
                ps.setInt(50, fmPlayer.getGoalKeeperAttributes().getCommunication());
                ps.setInt(51, fmPlayer.getGoalKeeperAttributes().getEccentricity());
                ps.setInt(52, fmPlayer.getGoalKeeperAttributes().getHandling());
                ps.setInt(53, fmPlayer.getGoalKeeperAttributes().getKicking());
                ps.setInt(54, fmPlayer.getGoalKeeperAttributes().getOneOnOnes());
                ps.setInt(55, fmPlayer.getGoalKeeperAttributes().getReflexes());
                ps.setInt(56, fmPlayer.getGoalKeeperAttributes().getRushingOut());
                ps.setInt(57, fmPlayer.getGoalKeeperAttributes().getTendencyToPunch());
                ps.setInt(58, fmPlayer.getGoalKeeperAttributes().getThrowing());
                ps.setInt(59, fmPlayer.getHiddenAttributes().getConsistency());
                ps.setInt(60, fmPlayer.getHiddenAttributes().getDirtiness());
                ps.setInt(61, fmPlayer.getHiddenAttributes().getImportantMatches());
                ps.setInt(62, fmPlayer.getHiddenAttributes().getInjuryProneness());
                ps.setInt(63, fmPlayer.getHiddenAttributes().getVersatility());
                ps.setInt(64, fmPlayer.getPosition().getGoalkeeper());
                ps.setInt(65, fmPlayer.getPosition().getDefenderCentral());
                ps.setInt(66, fmPlayer.getPosition().getDefenderLeft());
                ps.setInt(67, fmPlayer.getPosition().getDefenderRight());
                ps.setInt(68, fmPlayer.getPosition().getWingBackLeft());
                ps.setInt(69, fmPlayer.getPosition().getWingBackRight());
                ps.setInt(70, fmPlayer.getPosition().getDefensiveMidfielder());
                ps.setInt(71, fmPlayer.getPosition().getMidfielderLeft());
                ps.setInt(72, fmPlayer.getPosition().getMidfielderRight());
                ps.setInt(73, fmPlayer.getPosition().getMidfielderCentral());
                ps.setInt(74, fmPlayer.getPosition().getAttackingMidCentral());
                ps.setInt(75, fmPlayer.getPosition().getAttackingMidLeft());
                ps.setInt(76, fmPlayer.getPosition().getAttackingMidRight());
                ps.setInt(77, fmPlayer.getPosition().getStriker());
                ps.setString(78, fmPlayer.getFirstName());
                ps.setString(79, fmPlayer.getLastName());
                ps.setString(80, String.valueOf(fmPlayer.getBirth()));
                ps.setString(81, fmPlayer.getNationName());
                ps.setInt(82, fmPlayer.getFmUid());
                ps.setString(83, fmPlayer.getFmVersion().getValue());
            }

            @Override
            public int getBatchSize() {
                return fmPlayers.size();
            }
        });
    }

}
