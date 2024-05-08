package com.mash.aoptracktime.service;

import com.mash.aoptracktime.aspect.tracktime.annotation.TrackAsyncTime;
import com.mash.aoptracktime.aspect.tracktime.annotation.TrackTime;
import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.repository.EmployeesRepository;
import com.mash.aoptracktime.utils.ThreadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service to manipulate with employees {@link Employee}
 *
 * @author Mikhail Shamanov
 */
@Service
@RequiredArgsConstructor
public class EmployeesService {
    private final EmployeesRepository repository;

    /**
     * Saves employee.
     *
     * @param employee entity
     * @return saved entity
     */
    @TrackTime
    public Employee save(Employee employee) {
        ThreadUtils.sleep((long) Math.floor(Math.random() * 300), TimeUnit.MILLISECONDS);
        return this.repository.save(employee);
    }

    /**
     * Saves employee asynchronously.
     *
     * @param employee entity
     * @return saved entity
     */
    @TrackAsyncTime
    @Async
    public CompletableFuture<Employee> saveAsync(Employee employee) {
        ThreadUtils.sleep((long) Math.floor(Math.random() * 300), TimeUnit.MILLISECONDS);
        return CompletableFuture.completedFuture(this.repository.save(employee)).thenApply(e -> {
            if (Math.random() > 0.85) {
                throw new RuntimeException("Something went wrong");
            }
            return e;
        });
    }

    /**
     * Looks for an employee by id.
     *
     * @param id unique entity id
     * @return entity or null if not found
     */
    public Employee findById(Long id) {
        return this.repository.findById(id).orElse(null);
    }
}