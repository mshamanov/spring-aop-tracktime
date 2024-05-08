package com.mash.aoptracktime;

import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.generator.RandomEmployeesGenerator;
import com.mash.aoptracktime.service.EmployeesService;
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
    public CommandLineRunner commandLineRunner(EmployeesService service, RandomEmployeesGenerator generator) {
        return args -> {
            generator.generateAsFuture(10000L);
            generator.generateAsCompletableFuture(10000L);

            for (Employee employee : generator.generate(100L)) {
                if (Math.random() < 0.5) {
                    service.save(employee);
                } else {
                    service.saveAsync(employee);
                }
            }
        };
    }
}
