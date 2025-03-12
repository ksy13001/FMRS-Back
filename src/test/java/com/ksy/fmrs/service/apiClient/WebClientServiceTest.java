package com.ksy.fmrs.service.apiClient;

import ch.qos.logback.core.util.TimeUtil;
import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.apiFootball.SquadApiResponseDto;
import com.ksy.fmrs.service.FootballApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@SpringBootTest
class WebClientServiceTest {
//    @Autowired
//    @Qualifier("restClientService")  // 빈 이름이나 qualifier로 구분
//    private ApiClientService restApiClientService;
//
//    // WebClient 기반 구현체 (예: WebClientApiClientService)
//    @Autowired
//    @Qualifier("webClientService")
//    private ApiClientService webApiClientService;
//
//    @Test
//    @DisplayName("2m 22s")
//    void restClientServiceTest() {
//        for(int i = 40; i<=100;i++){
//            restApiClientService.getApiResponse(UrlEnum.buildSquadUrl(i), SquadApiResponseDto.class);
//        }
//    }
//
//    @Test
//    @DisplayName("2m 23s")
//    void webClientServiceTest() {
//        List<String> names =  new ArrayList<>();
//        for(int i = 40; i<=100;i++){
//            SquadApiResponseDto squadApiResponseDto =
//                    webApiClientService.getApiResponse(UrlEnum.buildSquadUrl(i), SquadApiResponseDto.class);
//            names.add(squadApiResponseDto.getResponse().getFirst().getTeam().getName());
//        }
//        System.out.println(names);
//    }
@Test
void webClientTest1() {
    long start = System.currentTimeMillis();

    // 각 method를 Runnable로 감싸서 별도의 스레드에서 실행하도록 지정합니다.
    Mono<Object> m1 = Mono.fromRunnable(() -> {
        try {
            method1();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).subscribeOn(Schedulers.parallel());

    Mono<Object> m2 = Mono.fromRunnable(() -> {
        try {
            method2();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).subscribeOn(Schedulers.parallel());

    Mono<Object> m3 = Mono.fromRunnable(() -> {
        try {
            method3();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).subscribeOn(Schedulers.parallel());

    // Mono.when()을 사용해 모든 Mono가 완료될 때까지 기다립니다.
    Mono.when(m1, m2, m3).block();

    long elapsed = System.currentTimeMillis() - start;
    System.out.println("Total elapsed time: " + elapsed + " ms");
}

    private void method1() throws InterruptedException {
        System.out.println("method1 시작");
        Thread.sleep(5000);
        System.out.println("method1 종료");
    }

    private void method2() throws InterruptedException {
        System.out.println("method2 시작");
        Thread.sleep(5000);
        System.out.println("method2 종료");
    }

    private void method3() throws InterruptedException {
        System.out.println("method3 시작");
        Thread.sleep(5000);
        System.out.println("method3 종료");
    }
}