package com.mash.aoptracktime;

import com.mash.aoptracktime.service.EmployeesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;

@EnableAsync
@SpringBootApplication
@Slf4j
public class AopTrackTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AopTrackTimeApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(EmployeesService employeesService) {
        return (args) -> {
            final int employeesNumber = 10000;

            /* Sync call */
            employeesService.getRandomEmployees(employeesNumber);

            /* Async call */
            employeesService.fillWithRandomEmployeesAsyncReturningVoid(new ArrayList<>(), employeesNumber);
            employeesService.getRandomEmployeesAsyncAsCompletableFuture(employeesNumber);
            employeesService.getRandomEmployeesAsyncAsFuture(employeesNumber);
        };
    }
}
