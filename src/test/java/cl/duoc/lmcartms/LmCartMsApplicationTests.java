package cl.duoc.lmcartms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.main.allow-bean-definition-overriding=true"
})
//@EnableAutoConfiguration(exclude = {
//        DataSourceAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class
//})
@ActiveProfiles("test")
class LmCartMsApplicationTests {

    @Test
    void contextLoads() {
    }

}
