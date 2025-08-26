package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.LeagueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO league
          (league_api_id, name, logo_url, current_season, standing, start_date, end_date, league_type)
        VALUES
          (:apiId, :name, :logoUrl, :currentSeason, :standing, :startDate, :endDate, :leagueType)
        ON DUPLICATE KEY UPDATE
          name = VALUES(name),
          logo_url = VALUES(logo_url),
          current_season = VALUES(current_season),
          standing = VALUES(standing),
          start_date = VALUES(start_date),
          end_date = VALUES(end_date),
          league_type = VALUES(league_type)
        """, nativeQuery = true)
    void upsertLeague(Integer apiId, String name, String logoUrl, Integer currentSeason, Boolean standing, LocalDate startDate, LocalDate endDate, String leagueType);

    List<League> findLeaguesByLeagueType(LeagueType leagueType);

    Optional<League> findLeagueByLeagueApiId(Integer leagueApiId);

    @Query("SELECT l.leagueApiId FROM League l")
    Set<Integer> findAllLeagueApiIds();

    @Query("SELECT l FROM League l " +
            "WHERE l.teams IS EMPTY")
    List<League> findUnassignedLeagues();

    @Query("SELECT l.leagueApiId FROM League l " +
            "WHERE l.startDate IS NULL" +
            "   OR l.endDate IS NULL " +
            "   OR :now < l.startDate " +
            "   OR :now > l.endDate")
    List<Integer> findLeaguesApiIdsOutsideSeason(LocalDate now);

    @Query("SELECT l FROM League l " +
            "WHERE l.startDate IS NULL" +
            "   OR l.endDate IS NULL " +
//            "   OR :now < l.startDate " +
            "   OR :now > l.endDate")
    List<League> findLeaguesIdsOutsideSeasonV1(LocalDate now);
}
