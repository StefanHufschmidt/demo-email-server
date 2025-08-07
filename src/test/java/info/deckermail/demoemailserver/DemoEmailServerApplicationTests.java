package info.deckermail.demoemailserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DemoEmailServerApplicationTests {

    @Test
    void contextLoads() {
        var am = ApplicationModules.of(DemoEmailServerApplication.class);
        am.verify();

        System.out.println(am);

        new Documenter(am).writeDocumentation();
    }

}
