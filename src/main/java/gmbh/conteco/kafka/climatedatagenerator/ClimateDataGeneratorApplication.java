package gmbh.conteco.kafka.climatedatagenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ClimateDataGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClimateDataGeneratorApplication.class, args);
    }
}