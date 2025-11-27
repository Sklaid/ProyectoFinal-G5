package com.techcorp.devops.service;

import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.dto.EmployeeDTO;
import com.techcorp.devops.dto.EmployeeUpdateDTO;
import com.techcorp.devops.entity.Employee;
import com.techcorp.devops.exception.EntityNotFoundException;
import com.techcorp.devops.exception.ValidationException;
import com.techcorp.devops.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        log.debug("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        log.debug("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id));
        return convertToDTO(employee);
    }
    
    public EmployeeDTO createEmployee(EmployeeCreateDTO createDTO) {
        log.debug("Creating new employee with email: {}", createDTO.getEmail());
        
        // Validate email format
        validateEmailFormat(createDTO.getEmail());
        
        // Check for duplicate email
        if (employeeRepository.existsByEmail(createDTO.getEmail())) {
            throw new ValidationException("Employee with email " + createDTO.getEmail() + " already exists");
        }
        
        Employee employee = Employee.builder()
                .firstName(createDTO.getFirstName())
                .lastName(createDTO.getLastName())
                .email(createDTO.getEmail())
                .phone(createDTO.getPhone())
                .gender(createDTO.getGender())
                .department(createDTO.getDepartment())
                .level(createDTO.getLevel())
                .skills(createDTO.getSkills() != null ? new HashSet<>(createDTO.getSkills()) : new HashSet<>())
                .hireDate(createDTO.getHireDate())
                .build();
        
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Created employee with id: {}", savedEmployee.getId());
        
        return convertToDTO(savedEmployee);
    }
    
    public EmployeeDTO updateEmployee(Long id, EmployeeUpdateDTO updateDTO) {
        log.debug("Updating employee with id: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id));
        
        // Validate email format
        validateEmailFormat(updateDTO.getEmail());
        
        // Check for duplicate email (excluding current employee)
        if (!employee.getEmail().equals(updateDTO.getEmail()) && 
            employeeRepository.existsByEmail(updateDTO.getEmail())) {
            throw new ValidationException("Employee with email " + updateDTO.getEmail() + " already exists");
        }
        
        employee.setFirstName(updateDTO.getFirstName());
        employee.setLastName(updateDTO.getLastName());
        employee.setEmail(updateDTO.getEmail());
        employee.setPhone(updateDTO.getPhone());
        employee.setGender(updateDTO.getGender());
        employee.setDepartment(updateDTO.getDepartment());
        employee.setLevel(updateDTO.getLevel());
        employee.setSkills(updateDTO.getSkills() != null ? new HashSet<>(updateDTO.getSkills()) : new HashSet<>());
        employee.setHireDate(updateDTO.getHireDate());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Updated employee with id: {}", updatedEmployee.getId());
        
        return convertToDTO(updatedEmployee);
    }
    
    public void deleteEmployee(Long id) {
        log.debug("Deleting employee with id: {}", id);
        
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee", id);
        }
        
        employeeRepository.deleteById(id);
        log.info("Deleted employee with id: {}", id);
    }
    
    private void validateEmailFormat(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }
    
    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .gender(employee.getGender())
                .department(employee.getDepartment())
                .level(employee.getLevel())
                .skills(employee.getSkills())
                .hireDate(employee.getHireDate())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
