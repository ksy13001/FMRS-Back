//package com.ksy.fmrs.repository;
//
//import com.ksy.fmrs.domain.enums.MappingStatus;
//import com.ksy.fmrs.domain.player.Player;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@SpringBootTest(
//        properties = {"api-football.key=test_key", "api-football.host=test_host"}
//)
//class BulkRepositoryTest {
//
//    @Autowired
//    private BulkRepository bulkRepository;
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    @DisplayName("Player List bulk insert 테스트")
//    void bulkInsertPlayers_valid(){
//        // given
//        List<Player> players = new ArrayList<>();
//        for(int i = 0; i < 1000; i++){
//            Player player = createPlayer(i, "f", "l", "n1", "n1", LocalDate.now(),
//                    180, 180, "image", MappingStatus.UNMAPPED);
//            players.add(player);
//        }
//        // when
//        bulkRepository.bulkInsertPlayers(players);
//        // then
//        TypedQuery<Player> result = entityManager.createQuery("SELECT p FROM Player p ", Player.class);
//        Assertions.assertThat(result.getResultList()).hasSize(1000);
//    }
//
//    private Player createPlayer(Integer playerApiId, String firstName, String lastName, String nationName, String nationLogo, LocalDate birth, int height, int weight, String imageUrl, MappingStatus mappingStatus){
//        return Player.builder()
//                .playerApiId(playerApiId)
//                .firstName(firstName)
//                .lastName(lastName)
//                .nationName(nationName)
//                .nationLogoUrl(nationLogo)
//                .birth(birth)
//                .height(height)
//                .weight(weight)
//                .imageUrl(imageUrl)
//                .mappingStatus(mappingStatus)
//                .build();
//    }
//}