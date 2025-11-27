package com.techcorp.devops.property;

import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.dto.EmployeeDTO;
import com.techcorp.devops.dto.EmployeeUpdateDTO;
import com.techcorp.devops.exception.EntityNotFoundException;
import com.techcorp.devops.service.EmployeeService;
import net.jqwik.api.*;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@JqwikSpringSupport
@Transactional
public class EmployeeCRUDPropertiesTest {
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * Feature: devops-enterprise-platform, Property 6: CRUD consistency - Create and Read
     * Validates: Requirements 2.1, 2.2
     */
    @Property(tries = 100)
    void createThenRead_ShouldReturnSameData(@ForAll("validEmployees") EmployeeCreateDTO createDTO) {
        // Act - Create employee
        EmployeeDTO created = employeeService.createEmployee(createDTO);
        
        // Act - Read employee
        EmployeeDTO retrieved = employeeService.getEmployeeById(created.getId());
        
        // Assert - Data should match
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(createDTO.getFirstName(), retrieved.getFirstName());
        assertEquals(createDTO.getLastName(), retrieved.getLastName());
        assertEquals(createDTO.getEmail(), retrieved.getEmail());
        assertEquals(createDTO.getPhone(), retrieved.getPhone());
        assertEquals(createDTO.getGender(), retrieved.getGender());
        assertEquals(createDTO.getDepartment(), retrieved.getDepartment());
        assertEquals(createDTO.getLevel(), retrieved.getLevel());
        assertEquals(createDTO.getSkills(), retrieved.getSkills());
        assertEquals(createDTO.getHireDate(), retrieved.getHireDate());
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 7: CRUD consistency - Update
     * Validates: Requirements 2.3
     */
    @Property(tries = 100)
    void updateThenRead_ShouldReturnUpdatedData(
            @ForAll("validEmployees") EmployeeCreateDTO createDTO,
            @ForAll("validEmployees") EmployeeCreateDTO updateData) {
        // Arrange - Create initial employee
        EmployeeDTO created = employeeService.createEmployee(createDTO);
        
        // Prepare update DTO
        EmployeeUpdateDTO updateDTO = EmployeeUpdateDTO.builder()
                .firstName(updateData.getFirstName())
                .lastName(updateData.getLastName())
                .email(updateData.getEmail())
                .phone(updateData.getPhone())
                .gender(updateData.getGender())
                .department(updateData.getDepartment())
                .level(updateData.getLevel())
                .skills(updateData.getSkills())
                .hireDate(updateData.getHireDate())
                .build();
        
        // Act - Update employee
        employeeService.updateEmployee(created.getId(), updateDTO);
        
        // Act - Read employee
        EmployeeDTO retrieved = employeeService.getEmployeeById(created.getId());
        
        // Assert - Data should match updated values
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(updateDTO.getFirstName(), retrieved.getFirstName());
        assertEquals(updateDTO.getLastName(), retrieved.getLastName());
        assertEquals(updateDTO.getEmail(), retrieved.getEmail());
        assertEquals(updateDTO.getPhone(), retrieved.getPhone());
        assertEquals(updateDTO.getGender(), retrieved.getGender());
        assertEquals(updateDTO.getDepartment(), retrieved.getDepartment());
        assertEquals(updateDTO.getLevel(), retrieved.getLevel());
        assertEquals(updateDTO.getSkills(), retrieved.getSkills());
        assertEquals(updateDTO.getHireDate(), retrieved.getHireDate());
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 8: CRUD consistency - Delete
     * Validates: Requirements 2.4
     */
    @Property(tries = 100)
    void deleteThenRead_ShouldThrowNotFoundException(@ForAll("validEmployees") EmployeeCreateDTO createDTO) {
        // Arrange - Create employee
        EmployeeDTO created = employeeService.createEmployee(createDTO);
        Long employeeId = created.getId();
        
        // Act - Delete employee
        employeeService.deleteEmployee(employeeId);
        
        // Assert - Reading deleted employee should throw exception
        assertThrows(EntityNotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 9: Invalid data is rejected
     * Validates: Requirements 2.5
     */
    @Property(tries = 100)
    void createWithInvalidEmail_ShouldReject(@ForAll("invalidEmailEmployees") EmployeeCreateDTO createDTO) {
        // Act & Assert - Creating with invalid email should throw exception
        assertThrows(Exception.class, () -> {
            employeeService.createEmployee(createDTO);
        });
    }
    
    @Provide
    Arbitrary<EmployeeCreateDTO> validEmployees() {
        return EmployeeGenerator.validEmployees();
    }
    
    @Provide
    Arbitrary<EmployeeCreateDTO> invalidEmailEmployees() {
        return EmployeeGenerator.invalidEmailEmployees();
    }
}
