package co.ohmygoods;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class OhMyGoodsApplicationTest {

    @Test
    void contextLoads() {}

    @Test
    void applicationModule() {
        ApplicationModules.of(OhMyGoodsApplication.class).verify();
    }
}