package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.TransferType;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag("performance")
@ActiveProfiles("test")
@SpringBootTest
class TransferServicePerformanceTest {

    private final static int TOTAL = 10;
    @Autowired
    TransferService transferService;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        transferRepository.deleteAll();
        List<Player> players = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < TOTAL; i++) {
            players.add(Player.builder().playerApiId(i).build());
            teams.add(Team.builder().teamApiId(i).build());
            teams.add(Team.builder().teamApiId(i + TOTAL).build());
        }
        playerRepository.saveAll(players);
        teamRepository.saveAll(teams);
    }

    @Test
    @DisplayName("saveAllV1 vs saveAllV2")
    @Transactional
    void saveV1_V2() {
        // given
        StopWatch sw = new StopWatch();
        List<TransferRequestDto> dtos = createDtos();
        // when
        System.out.println("V1 start -----------------------------------");
        sw.start("saveAllV1");
        transferService.saveAllV1(dtos);
        sw.stop();

        System.out.println("V2 start -----------------------------------");
        sw.start("saveAllV2");
        transferService.saveAll(dtos);
        sw.stop();

        // then
        System.out.println(sw.prettyPrint());
        System.out.println(sw.getTotalTimeMillis());
    }

    private List<TransferRequestDto> createDtos() {
        List<TransferRequestDto> dtos = new ArrayList<>();
        for (int i = 0; i < TOTAL; i++) {
            dtos.add(new TransferRequestDto(
                    i, i, i + TOTAL, TransferType.PERMANENT, 300d, "EUR", LocalDate.now(), LocalDateTime.now()
            ));
        }
        return dtos;
    }
}
