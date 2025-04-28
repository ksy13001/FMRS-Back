package com.ksy.fmrs.repository;

import com.ksy.fmrs.config.RedisConfig;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class BulkRepositoryTest {

    @Autowired
    private BulkRepository bulkRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Player List bulk insert 테스트")
    void bulkInsertPlayers_valid(){
        // given
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < 1000; i++){
            Player player = createPlayer(i, "f", "l", "n1", "n1", LocalDate.now(),
                    180, 180, "image",PlayerMappingStatus.UNMAPPED);
            players.add(player);
        }
        // when
        bulkRepository.bulkInsertPlayers(players);
        // then
        TypedQuery<Player> result = entityManager.createQuery("SELECT p FROM Player p ", Player.class);
        Assertions.assertThat(result.getResultList()).hasSize(1000);
    }

    private Player createPlayer(Integer id, String firstName, String lastName, String nationName, String nationLogo, LocalDate birth, int height, int weight, String imageUrl, PlayerMappingStatus mappingStatus){
        return Player.builder()
                .playerApiId(id)
                .firstName(firstName)
                .lastName(lastName)
                .nationName(nationName)
                .nationLogoUrl(nationLogo)
                .birth(birth)
                .height(height)
                .weight(weight)
                .imageUrl(imageUrl)
                .mappingStatus(mappingStatus)
                .build();
    }
}