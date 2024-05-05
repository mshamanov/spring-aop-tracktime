package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.repository.EmployeesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeesService {
    private final EmployeesRepository repository;

    public Employee save(Employee employee) {
        return this.repository.save(employee);
    }

    public Employee findById(Long id) {
        return this.repository.findById(id).orElse(null);
    }
}