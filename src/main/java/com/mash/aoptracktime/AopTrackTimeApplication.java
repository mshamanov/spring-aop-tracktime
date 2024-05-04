package com.mash.aoptracktime;

import com.mash.aoptracktime.service.EmployeesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@SpringBootApplication
public class AopTrackTimeApplication {
    private final EmployeesService employeesService;

    public static void main(String[] args) {
        SpringApplication.run(AopTrackTimeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        final int employeesNumber = 10000;
        this.employeesService.getRandomEmployees(employeesNumber);

        this.employeesService.fillWithRandomEmployeesAsyncReturningVoid(new ArrayList<>(), employeesNumber);
        this.employeesService.getRandomEmployeesAsyncAsCompletableFuture(employeesNumber);
        this.employeesService.getRandomEmployeesAsyncAsFuture(employeesNumber);
    }
}
