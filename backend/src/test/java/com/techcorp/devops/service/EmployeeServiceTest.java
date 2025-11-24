package com.techcorp.devops.service;

import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.dto.EmployeeDTO;
import com.techcorp.devops.dto.EmployeeUpdateDTO;
import com.techcorp.devops.entity.Department;
import com.techcorp.devops.entity.Employee;
import com.techcorp.devops.entity.Gender;
import com.techcorp.devops.entity.Level;
import com.techcorp.devops.exception.EntityNotFoundException;
import com.techcorp.devops.exception.ValidationException;
import com.techcorp.devops.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    
    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private EmployeeService employeeService;
    
    private EmployeeCreateDTO validCreateDTO;
    private Employee validEmployee;
    
    @BeforeEach
    void setUp() {
        Set<String> skills = new HashSet<>();
        skills.add("Java");
        skills.add("Spring Boot");
        
        validCreateDTO = EmployeeCreateDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .gender(Gender.MALE)
                .department(Department.IT)
                .level(Level.SENIOR)
                .skills(skills)
                .hireDate(LocalDate.now())
                .build();
        
        validEmployee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .gender(Gender.MALE)
                .department(Department.IT)
                .level(Level.SENIOR)
                .skills(skills)
                .hireDate(LocalDate.now())
                .build();
    }
    
    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Arrange
        Employee employee2 = Employee.builder()
                .id(2L)
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
        
        List<Employee> employees = Arrays.asList(validEmployee, employee2);
        when(employeeRepository.findAll()).thenReturn(employees);
        
        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(validEmployee.getEmail(), result.get(0).getEmail());
        assertEquals(employee2.getEmail(), result.get(1).getEmail());
        verify(employeeRepository).findAll();
    }
    
    @Test
    void createEmployee_WithValidData_ShouldSucceed() {
        // Arrange
        when(employeeRepository.existsByEmail(validCreateDTO.getEmail())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);
        
        // Act
        EmployeeDTO result = employeeService.createEmployee(validCreateDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(validEmployee.getId(), result.getId());
        assertEquals(validCreateDTO.getEmail(), result.getEmail());
        verify(employeeRepository).existsByEmail(validCreateDTO.getEmail());
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void createEmployee_WithDuplicateEmail_ShouldThrowValidationException() {
        // Arrange
        when(employeeRepository.existsByEmail(validCreateDTO.getEmail())).thenReturn(true);
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            employeeService.createEmployee(validCreateDTO);
        });
        verify(employeeRepository).existsByEmail(validCreateDTO.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void createEmployee_WithInvalidEmail_ShouldThrowValidationException() {
        // Arrange
        validCreateDTO.setEmail("invalid-email");
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            employeeService.createEmployee(validCreateDTO);
        });
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void getEmployeeById_WhenExists_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        
        // Act
        EmployeeDTO result = employeeService.getEmployeeById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(validEmployee.getId(), result.getId());
        assertEquals(validEmployee.getEmail(), result.getEmail());
        verify(employeeRepository).findById(1L);
    }
    
    @Test
    void getEmployeeById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });
        verify(employeeRepository).findById(1L);
    }
    
    @Test
    void updateEmployee_WithValidData_ShouldSucceed() {
        // Arrange
        EmployeeUpdateDTO updateDTO = EmployeeUpdateDTO.builder()
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
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.existsByEmail(updateDTO.getEmail())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(validEmployee);
        
        // Act
        EmployeeDTO result = employeeService.updateEmployee(1L, updateDTO);
        
        // Assert
        assertNotNull(result);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void updateEmployee_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        EmployeeUpdateDTO updateDTO = EmployeeUpdateDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .gender(Gender.FEMALE)
                .department(Department.HR)
                .level(Level.LEAD)
                .hireDate(LocalDate.now())
                .build();
        
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            employeeService.updateEmployee(1L, updateDTO);
        });
        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }
    
    @Test
    void deleteEmployee_WhenExists_ShouldSucceed() {
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);
        
        // Act
        employeeService.deleteEmployee(1L);
        
        // Assert
        verify(employeeRepository).existsById(1L);
        verify(employeeRepository).deleteById(1L);
    }
    
    @Test
    void deleteEmployee_WhenNotExists_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            employeeService.deleteEmployee(1L);
        });
        verify(employeeRepository).existsById(1L);
        verify(employeeRepository, never()).deleteById(1L);
    }
}
