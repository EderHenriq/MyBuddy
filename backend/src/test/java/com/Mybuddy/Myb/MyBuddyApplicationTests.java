package com.Mybuddy.Myb;

import com.Mybuddy.Myb.Config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class MyBuddyApplicationTests {

    @Test
    void contextLoads() {
    }
}