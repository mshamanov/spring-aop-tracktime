package com.mash.aoptracktime.service;

import com.mash.aoptracktime.entity.Employee;
import com.mash.aoptracktime.repository.EmployeesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeesServiceTest {

    @Mock
    EmployeesRepository repository;

    @InjectMocks
    EmployeesService service;

    @Test
    void saveEmployee_shouldCallRepository() {
        Employee employee = mock(Employee.class);
        when(this.repository.save(employee)).thenReturn(employee);

        Employee saved = this.service.save(employee);

        verify(this.repository).save(employee);
        assertEquals(employee, saved);
    }

    @Test
    void findEmployeeById_shouldCallRepository() {
        Employee employee = mock(Employee.class);
        when(this.repository.findById(1L)).thenReturn(Optional.of(employee));

        Employee saved = this.service.findById(1L);

        verify(this.repository).findById(1L);
        assertEquals(employee, saved);
    }
}