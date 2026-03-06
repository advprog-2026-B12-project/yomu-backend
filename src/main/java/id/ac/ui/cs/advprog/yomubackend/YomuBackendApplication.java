package id.ac.ui.cs.advprog.yomubackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YomuBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YomuBackendApplication.class, args);
    }

}
