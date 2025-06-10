//package com.ksy.fmrs.repository.Player;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ksy.fmrs.domain.player.PlayerRaw;
//import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class PlayerRawRepositoryTest {
//
//    @Autowired
//    private PlayerRawRepository playerRawRepository;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Test
//    void 선수명_파싱() throws JsonProcessingException {
//        // given
//        PlayerRaw playerRaw=  playerRawRepository.findById(1)
//                .orElseThrow(()->new IllegalArgumentException("cannot find PlayerRaw"));
//        LeagueApiPlayersDto leagueApiPlayersDto =
//                objectMapper.readValue(playerRaw.getJsonRaw(), LeagueApiPlayersDto.class);
//        // when
//
//        // then
//        System.out.println("Player name = "+ leagueApiPlayersDto.response().getFirst().player().name());
//    }
//}