package com.mash.aoptracktime.generator;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.entity.Employee;
import net.datafaker.Faker;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Generator of random employees {@link Employee}.
 *
 * @author Mikhail Shamanov
 */
@Component
public class RandomEmployeesGenerator implements RandomEntityGenerator<Employee> {
    private final Faker faker = new Faker();

    /**
     * Asynchronously generates random employees.
     *
     * @param limit amount of employees to generate
     * @return randomly generated employees
     */
    @Async
    @TrackAsyncTime
    public CompletableFuture<List<Employee>> generateAsCompletableFuture(Long limit) {
        return CompletableFuture.supplyAsync(() -> this.generate(limit));
    }

    /**
     * Asynchronously generates random employees.
     *
     * @param limit amount of employees to generate
     * @return randomly generated employees
     */
    @Async
    @TrackAsyncTime
    public Future<List<Employee>> generateAsFuture(Long limit) {
        return new AsyncResult<>(this.generate(limit));
    }

    /**
     * Generates random employees.
     *
     * @param limit amount of employees to generate
     * @return randomly generated employees
     */
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
