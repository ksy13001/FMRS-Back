package com.ksy.fmrs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FmrsApplicationTests {

    @Test
    void contextLoads() {
        // ensures the Spring context starts with the test profile
    }
}
