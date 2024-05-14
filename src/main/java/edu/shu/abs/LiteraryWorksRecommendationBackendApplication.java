package edu.shu.abs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LiteraryWorksRecommendationBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiteraryWorksRecommendationBackendApplication.class, args);
    }

}
