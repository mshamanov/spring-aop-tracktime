package com.mash.aoptracktime.service;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.utils.ThreadUtils;
import net.datafaker.Faker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
        return this.generateRandomEmployees(limit);
    }

    @Async
    @TrackAsyncTime(groupName = "async")
    public CompletableFuture<List<Employee>> getRandomEmployeesAsyncAsCompletableFuture(long limit) {
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
    public void fillWithRandomEmployeesAsyncReturningVoid(Collection<? super Employee> collection, long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        collection.addAll(this.generateRandomEmployees(limit));
    }

    private List<Employee> generateRandomEmployees(long limit) {
        return Stream.generate(() -> new Employee(this.faker.internet()
                .emailAddress(), this.faker.name()
                .firstName(), this.faker.name().lastName(), this.faker.date()
                .birthdayLocalDate(18, 50))).limit(limit).toList();
    }
}