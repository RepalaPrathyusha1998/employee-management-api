package com.freelance.employeemanagement.employee;

import tools.jackson.databind.ObjectMapper;
import com.freelance.employeemanagement.exception.GlobalExceptionHandler;
import com.freelance.employeemanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_shouldReturnEmployees() throws Exception {
        List<Employee> employees = List.of(
                new Employee("John Smith", "john@example.com", "Software Engineer"),
                new Employee("Jane Doe", "jane@example.com", "Developer")
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        Employee employee =
                new Employee("John Smith", "john@example.com", "Software Engineer");

        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("Software Engineer"));

        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    void getEmployeeById_shouldReturn404WhenNotFound() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void createEmployee_shouldReturn201() throws Exception {
        Employee employee =
                new Employee("John Smith", "john@example.com", "Software Engineer");

        when(employeeService.createEmployee(any(Employee.class)))
                .thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("Software Engineer"));

        verify(employeeService).createEmployee(any(Employee.class));
    }

    @Test
    void createEmployee_shouldReturn400ForInvalidData() throws Exception {
        Employee employee =
                new Employee("", "wrong-email", "");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(employeeService, never()).createEmployee(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployee() throws Exception {
        Employee employee =
                new Employee("John Updated", "john.updated@example.com", "Senior Developer");

        when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
                .thenReturn(employee);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.role").value("Senior Developer"));

        verify(employeeService).updateEmployee(eq(1L), any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldReturn204() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1L);
    }
}