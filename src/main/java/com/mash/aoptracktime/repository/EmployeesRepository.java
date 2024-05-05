package com.mash.aoptracktime.repository;

import com.mash.aoptracktime.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmployeesRepository extends CrudRepository<Employee, Long> {
}
