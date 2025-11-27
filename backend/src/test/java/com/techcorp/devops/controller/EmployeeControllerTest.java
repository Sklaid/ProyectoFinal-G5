package com.techcorp.devops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.dto.EmployeeDTO;
import com.techcorp.devops.dto.EmployeeUpdateDTO;
import com.techcorp.devops.entity.Department;
import com.techcorp.devops.entity.Gender;
import com.techcorp.devops.entity.Level;
import com.techcorp.devops.exception.EntityNotFoundException;
import com.techcorp.devops.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private EmployeeService employeeService;
    
    private EmployeeDTO employeeDTO;
    private EmployeeCreateDTO createDTO;
    private EmployeeUpdateDTO updateDTO;
    
    @BeforeEach
    void setUp() {
        employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .gender(Gender.MALE)
                .department(Department.IT)
                .level(Level.SENIOR)
                .skills(new HashSet<>(Arrays.asList("Java", "Spring Boot")))
                .hireDate(LocalDate.now())
                .build();
        
        createDTO = EmployeeCreateDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .gender(Gender.MALE)
                .department(Department.IT)
                .level(Level.SENIOR)
                .skills(new HashSet<>(Arrays.asList("Java", "Spring Boot")))
                .hireDate(LocalDate.now())
                .build();
        
        updateDTO = EmployeeUpdateDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .gender(Gender.FEMALE)
                .department(Department.HR)
                .level(Level.LEAD)
                .skills(new HashSet<>())
                .hireDate(LocalDate.now())
                .build();
    }
    
    @Test
    @WithMockUser
    void getAllEmployees_ShouldReturnEmployeeList() throws Exception {
        // Arrange
        List<EmployeeDTO> employees = Arrays.asList(employeeDTO);
        when(employeeService.getAllEmployees()).thenReturn(employees);
        
        // Act & Assert
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }
    
    @Test
    @WithMockUser
    void getEmployeeById_WhenExists_ShouldReturnEmployee() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }
    
    @Test
    @WithMockUser
    void getEmployeeById_WhenNotExists_ShouldReturn404() throws Exception {
        // Arrange
        when(employeeService.getEmployeeById(1L)).thenThrow(new EntityNotFoundException("Employee", 1L));
        
        // Act & Assert
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        when(employeeService.createEmployee(any(EmployeeCreateDTO.class))).thenReturn(employeeDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }
    
    @Test
    @WithMockUser
    void createEmployee_WithoutAdminRole_ShouldReturn403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_WithValidData_ShouldReturnUpdated() throws Exception {
        // Arrange
        EmployeeDTO updatedDTO = EmployeeDTO.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .gender(Gender.FEMALE)
                .department(Department.HR)
                .level(Level.LEAD)
                .skills(new HashSet<>())
                .hireDate(LocalDate.now())
                .build();
        
        when(employeeService.updateEmployee(eq(1L), any(EmployeeUpdateDTO.class))).thenReturn(updatedDTO);
        
        // Act & Assert
        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_WhenExists_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(employeeService).deleteEmployee(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }
}
