package com.ksy.fmrs.service.apiClient;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
class WebClientServiceTest {

//
//    @Test
//    void webClientTest1() {
//        long start = System.currentTimeMillis();
//
//        // 각 method를 Runnable로 감싸서 별도의 스레드에서 실행하도록 지정합니다.
//        Mono<Object> m1 = Mono.fromRunnable(() -> {
//            try {
//                method1();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).subscribeOn(Schedulers.parallel());
//
//        Mono<Object> m2 = Mono.fromRunnable(() -> {
//            try {
//                method2();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).subscribeOn(Schedulers.parallel());
//
//        Mono<Object> m3 = Mono.fromRunnable(() -> {
//            try {
//                method3();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).subscribeOn(Schedulers.parallel());
//
//        // Mono.when()을 사용해 모든 Mono가 완료될 때까지 기다립니다.
//        Mono.when(m1, m2, m3).block();
//
//        long elapsed = System.currentTimeMillis() - start;
//        System.out.println("Total elapsed time: " + elapsed + " ms");
//    }
//
//    private void method1() throws InterruptedException {
//        System.out.println("method1 시작");
//        Thread.sleep(5000);
//        System.out.println("method1 종료");
//    }
//
//    private void method2() throws InterruptedException {
//        System.out.println("method2 시작");
//        Thread.sleep(5000);
//        System.out.println("method2 종료");
//    }
//
//    private void method3() throws InterruptedException {
//        System.out.println("method3 시작");
//        Thread.sleep(5000);
//        System.out.println("method3 종료");
//    }
}