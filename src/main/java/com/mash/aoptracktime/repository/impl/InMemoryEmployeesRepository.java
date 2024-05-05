package com.mash.aoptracktime.repository.impl;

import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.repository.EmployeesRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

@Repository
public class InMemoryEmployeesRepository implements EmployeesRepository {
    private final Map<Long, Employee> data = new ConcurrentHashMap<>();

    @Override
    public <S extends Employee> S save(S entity) {
        this.data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Employee> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<Employee> findById(Long aLong) {
        return Optional.ofNullable(this.data.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return this.data.containsKey(aLong);
    }

    @Override
    public Iterable<Employee> findAll() {
        return this.data.values();
    }

    @Override
    public Iterable<Employee> findAllById(Iterable<Long> longs) {
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
        this.data.remove(aLong);
    }

    @Override
    public void delete(Employee entity) {
        this.deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        longs.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Employee> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.data.clear();
    }
}
