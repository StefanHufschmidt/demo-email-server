package info.deckermail.demoemailserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoEmailServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoEmailServerApplication.class, args);
    }

}
