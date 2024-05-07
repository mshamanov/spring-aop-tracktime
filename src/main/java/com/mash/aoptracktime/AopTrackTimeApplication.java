package com.mash.aoptracktime;

import com.mash.aoptracktime.generator.RandomEmployeesGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@AllArgsConstructor
@SpringBootApplication
public class AopTrackTimeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AopTrackTimeApplication.class, args);
    }

    @Profile("!test")
    @Bean
    public CommandLineRunner commandLineRunner(RandomEmployeesGenerator employeeRandomGenerator) {
        return args -> {
//            Long employeesNumber = 10000L;
//            employeeRandomGenerator.generate(employeesNumber);
//            employeeRandomGenerator.generateAsFuture(employeesNumber);
//            employeeRandomGenerator.generateAsCompletableFuture(employeesNumber);
        };
    }
}
