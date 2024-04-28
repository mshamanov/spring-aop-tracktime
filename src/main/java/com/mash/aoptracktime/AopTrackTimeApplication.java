package com.mash.aoptracktime;

import com.mash.aoptracktime.service.EmployeesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

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

            /* No Throwing Errors */

            /* Sync call */
             employeesService.getRandomEmployees(employeesNumber);

            /* Async call */
             employeesService.getRandomEmployeesAsyncReturningVoid(employeesNumber);
             employeesService.getRandomEmployeesAsCompletableFuture(employeesNumber);
             employeesService.getRandomEmployeesAsyncAsFuture(employeesNumber);

            /* Throwing Errors */

            /* Sync call */
            // employeesService.getRandomEmployeesThrowing(employeesNumber);

            /* Async call */
            // employeesService.getRandomEmployeesAsyncReturningVoidThrowing(employeesNumber);
            // employeesService.getRandomEmployeesAsCompletableFutureThrowing(employeesNumber);
            // employeesService.getRandomEmployeesAsyncAsFutureThrowing(employeesNumber);
        };
    }
}
