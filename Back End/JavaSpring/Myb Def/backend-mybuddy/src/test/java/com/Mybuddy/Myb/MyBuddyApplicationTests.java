package com.Mybuddy.Myb;

import com.Mybuddy.Myb.Config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class MyBuddyApplicationTests {

    @Test
    void contextLoads() {
    }
}