package com.freelance.employeemanagement.employee;

import com.freelance.employeemanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_shouldSaveEmployee() {
        Employee employee =
                new Employee("John Smith", "john@example.com", "Software Engineer");

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertEquals(employee, result);
        verify(employeeRepository).save(employee);
    }

    @Test
    void getAllEmployees_shouldReturnEmployees() {
        List<Employee> employees = List.of(
                new Employee("John Smith", "john@example.com", "Software Engineer"),
                new Employee("Jane Doe", "jane@example.com", "Developer")
        );

        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals(employees, result);
        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {
        Employee employee =
                new Employee("John Smith", "john@example.com", "Software Engineer");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals(employee, result);
        verify(employeeRepository).findById(1L);
    }

    @Test
    void getEmployeeById_shouldThrowExceptionWhenNotFound() {
        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(999L)
        );

        verify(employeeRepository).findById(999L);
    }

    @Test
    void updateEmployee_shouldUpdateAndSaveEmployee() {
        Employee existingEmployee =
                new Employee("John Smith", "john@example.com", "Developer");

        Employee updatedEmployee =
                new Employee("John Updated", "john.updated@example.com", "Senior Developer");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals("Senior Developer", result.getRole());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee() {
        when(employeeRepository.existsById(1L))
                .thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).existsById(1L);
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldThrowExceptionWhenNotFound() {
        when(employeeRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(999L)
        );

        verify(employeeRepository).existsById(999L);
        verify(employeeRepository, never()).deleteById(999L);
    }
}