package com.ksy.fmrs.repository;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.domain.League;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestQueryDSLConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // default 는 h2 사용
class LeagueRepositoryTest {

    @Autowired
    private LeagueRepository leagueRepository;

    @AfterEach
    void deleteAll() {
        leagueRepository.deleteAll();
    }

    @Test
    @DisplayName("리그 save")
    void save(){
        // given
        League league = League.builder()
                .name("League")
                .build();
        // when
        League savedLeague = leagueRepository.save(league);
        // then
        Assertions.assertThat(savedLeague).isEqualTo(league);
    }


    @Test
    @DisplayName("리그 saveAll")
    void saveAll(){
        // given
        League league1 = League.builder()
                .name("League1")
                .build();

        League league2 = League.builder()
                .name("League1")
                .build();

        League league3 = League.builder()
                .name("League1")
                .build();
        List<League> leagueList = Arrays.asList(league1, league2, league3);
        // when
        List<League> savedLeagueList = leagueRepository.saveAll(leagueList);
        // then
        Assertions.assertThat(savedLeagueList).hasSize(3);
    }
}