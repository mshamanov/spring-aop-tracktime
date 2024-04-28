package com.mash.aoptracktime.service;

import com.mash.aoptracktime.aspect.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.annotation.TrackTime;
import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.utils.ThreadUtils;
import net.datafaker.Faker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class EmployeesService {
    private final Faker faker;
    private final Map<String, Employee> employeeMap;

    public EmployeesService(Faker faker) {
        this.faker = faker;
        this.employeeMap = new HashMap<>();
    }

    public void addEmployee(String id, Employee employee) {
        this.employeeMap.put(id, employee);
    }

    public Employee getEmployee(String id) {
        return this.employeeMap.get(id);
    }

    @TrackTime(groupName = "sync")
    public List<Employee> getRandomEmployees(long limit) {
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        return generateRandomEmployees(limit);
    }

    @TrackTime(groupName = "sync")
    public List<Employee> getRandomEmployeesThrowing(long limit) {
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        if (limit > 0) {
            throw new RuntimeException("Something went wrong");
        }
        return this.generateRandomEmployees(limit);
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public CompletableFuture<List<Employee>> getRandomEmployeesAsCompletableFuture(long limit) {
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        return CompletableFuture.supplyAsync(() -> {
            ThreadUtils.sleep(5, TimeUnit.SECONDS);
            return this.generateRandomEmployees(limit);
        });
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public Future<List<Employee>> getRandomEmployeesAsyncAsFuture(long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        return new AsyncResult<>(this.generateRandomEmployees(limit));
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public void getRandomEmployeesAsyncReturningVoid(long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        this.generateRandomEmployees(limit);
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public CompletableFuture<List<Employee>> getRandomEmployeesAsCompletableFutureThrowing(long limit) {
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        return CompletableFuture.supplyAsync(() -> {
            ThreadUtils.sleep(5, TimeUnit.SECONDS);
            if (limit > 0) {
                throw new RuntimeException("Something went wrong");
            }
            return this.generateRandomEmployees(limit);
        });
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public Future<List<Employee>> getRandomEmployeesAsyncAsFutureThrowing(long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        this.generateRandomEmployees(limit);
        return AsyncResult.forExecutionException(new RuntimeException("Something went wrong"));
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public void getRandomEmployeesAsyncReturningVoidThrowing(long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        if (limit > 0) {
            throw new RuntimeException("Something went wrong");
        }
        this.generateRandomEmployees(limit);
    }

    private List<Employee> generateRandomEmployees(long limit) {
        return Stream.generate(() -> new Employee(this.faker.internet()
                .emailAddress(), this.faker.name()
                .firstName(), this.faker.name().lastName(), this.faker.date()
                .birthdayLocalDate(18, 50))).limit(limit).toList();
    }
}