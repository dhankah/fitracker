package com.fitracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "jwt.secret=test-secret-key"
})
class FitrackerApplicationTests {

    @Test
    void contextLoads() {
    }

}
