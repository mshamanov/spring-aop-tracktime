package com.mash.aoptracktime.generator;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.utils.ThreadUtils;
import net.datafaker.Faker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
public class RandomEmployeesGenerator implements RandomEntityGenerator<Employee> {
    private final Faker faker;

    public RandomEmployeesGenerator() {
        this.faker = new Faker();
    }

    @Async
    @TrackAsyncTime
    public CompletableFuture<List<Employee>> generateAsCompletableFuture(Long limit) {
        ThreadUtils.sleep(2, TimeUnit.SECONDS);
        return CompletableFuture.supplyAsync(() -> {
            ThreadUtils.sleep(5, TimeUnit.SECONDS);
            return this.generate(limit);
        });
    }

    @Async
    @TrackAsyncTime
    public Future<List<Employee>> generateAsFuture(Long limit) {
        ThreadUtils.sleep(5, TimeUnit.SECONDS);
        return new AsyncResult<>(this.generate(limit));
    }

    @TrackTime
    @Override
    public List<Employee> generate(Long limit) {
        return Stream.generate(() -> new Employee(
                        this.faker.random().nextLong(),
                        this.faker.internet().emailAddress(),
                        this.faker.name().firstName(),
                        this.faker.name().lastName(),
                        this.faker.date().birthdayLocalDate(18, 50)))
                .limit(limit)
                .toList();
    }
}
