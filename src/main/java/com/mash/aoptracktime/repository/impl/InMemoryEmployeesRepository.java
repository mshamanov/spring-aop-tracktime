package com.mash.aoptracktime.repository.impl;

import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.repository.EmployeesRepository;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * In-memory implementation of a repository {@link EmployeesRepository} used to keep data for employees {@link Employee}.
 *
 * @author Mikhail Shamanov
 */
@Repository
@NonNullApi
public class InMemoryEmployeesRepository implements EmployeesRepository {
    private final Map<Long, Employee> data = new ConcurrentHashMap<>();

    @Override
    public <S extends Employee> S save(S entity) {
        Objects.requireNonNull(entity);

        this.data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Employee> Iterable<S> saveAll(Iterable<S> entities) {
        Objects.requireNonNull(entities);

        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<Employee> findById(Long aLong) {
        Objects.requireNonNull(aLong);

        return Optional.ofNullable(this.data.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        Objects.requireNonNull(aLong);

        return this.data.containsKey(aLong);
    }

    @Override
    public Iterable<Employee> findAll() {
        return this.data.values();
    }

    @Override
    public Iterable<Employee> findAllById(Iterable<Long> longs) {
        Objects.requireNonNull(longs);

        return this.data.values()
                .stream()
                .filter(employee -> StreamSupport.stream(longs.spliterator(), false)
                        .anyMatch(l -> employee.getId().equals(l)))
                .toList();
    }

    @Override
    public long count() {
        return this.data.size();
    }

    @Override
    public void deleteById(Long aLong) {
        Objects.requireNonNull(aLong);

        this.data.remove(aLong);
    }

    @Override
    public void delete(Employee entity) {
        Objects.requireNonNull(entity);

        this.deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        Objects.requireNonNull(longs);

        longs.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Employee> entities) {
        Objects.requireNonNull(entities);

        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.data.clear();
    }
}
