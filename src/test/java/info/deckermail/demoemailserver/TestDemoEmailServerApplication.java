package info.deckermail.demoemailserver;

import org.springframework.boot.SpringApplication;

public class TestDemoEmailServerApplication {

    public static void main(String[] args) {
        SpringApplication.from(DemoEmailServerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
