//package com.ksy.fmrs.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
//
//import com.ksy.fmrs.dto.PlayerRealFootballStatDto;
//import com.ksy.fmrs.dto.TeamApiResponseDto;
//import com.ksy.fmrs.dto.PlayerStatisticsApiResponseDto;
//import com.ksy.fmrs.service.FootballApiService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.client.MockRestServiceServer;
//
//import java.util.Arrays;
//
//@SpringBootTest(
//        classes = FootballApiService.class,
//        properties = {
//                "api-football.key=dummyKey",
//                "api-football.host=dummyHost"
//        },
//        webEnvironment = SpringBootTest.WebEnvironment.NONE
//)
//@AutoConfigureMockRestServiceServer
//@TestPropertySource(properties = {
//        "api-football.key=dummyKey",
//        "api-football.host=dummyHost"
//})
//class FootballApiServiceTest {
//
//    @Autowired
//    private FootballApiService footballApiService;
//
//    @Autowired
//    private MockRestServiceServer server;
//
//    @Test
//    public void testGetPlayerRealStat() {
//        // ------------------------------
//        // Stub: 팀 API 호출 (팀 이름으로 팀 API ID 획득)
//        // 호출 URL: "https://v3.football.api-sports.io/teams?name=PSG"
//        // ------------------------------
//        String teamApiResponseJson = "{" +
//                "\"response\": [{" +
//                "\"team\": {" +
//                "\"id\": 100," +
//                "\"name\": \"Dummy Team\"," +
//                "\"code\":\"\"," +
//                "\"country\":\"\"," +
//                "\"founded\":0," +
//                "\"national\":false," +
//                "\"logo\":\"\"" +
//                "}" +
//                "}]" +
//                "}";
//        server.expect(requestTo("https://v3.football.api-sports.io/teams?name=PSG"))
//                .andRespond(withSuccess(teamApiResponseJson, MediaType.APPLICATION_JSON));
//
//        // ------------------------------
//        // Stub: 선수 통계 API 호출
//        // 호출 URL: "https://v3.football.api-sports.io/players?name=Lionel Messi&team=100"
//        // ------------------------------
//        String playerStatResponseJson = "{" +
//                "\"response\": [{" +
//                "\"statistics\": [{" +
//                "\"games\": {" +
//                "\"appearences\": 10," +
//                "\"rating\": \"7.5\"" +
//                "}," +
//                "\"goals\": {" +
//                "\"total\": 5," +
//                "\"assists\": 3" +
//                "}," +
//                "\"penalty\": {" +
//                "\"scored\": 2" +
//                "}" +
//                "}]" +
//                "}]" +
//                "}";
//        server.expect(requestTo("https://v3.football.api-sports.io/players?name=Lionel Messi&team=100"))
//                .andRespond(withSuccess(playerStatResponseJson, MediaType.APPLICATION_JSON));
//
//        // when
//        PlayerRealFootballStatDto result = footballApiService.getPlayerRealStat("Lionel Messi", "PSG");
//
//        // then
//        assertNotNull(result);
//        assertEquals(10, result.getGamesPlayed());
//        assertEquals(5, result.getGoal());
//        assertEquals(3, result.getAssist());
//        assertEquals(2, result.getPk());
//        assertEquals("7.5", result.getRating());
//
//        // 모든 stub 호출이 이루어졌는지 검증
//        server.verify();
//    }
//}
