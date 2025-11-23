package com.techcorp.devops.controller;

import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.dto.EmployeeDTO;
import com.techcorp.devops.dto.EmployeeUpdateDTO;
import com.techcorp.devops.dto.MessageResponse;
import com.techcorp.devops.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.debug("GET /api/employees - Fetching all employees");
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        log.debug("GET /api/employees/{} - Fetching employee by id", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO createDTO) {
        log.debug("POST /api/employees - Creating new employee");
        EmployeeDTO employee = employeeService.createEmployee(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDTO updateDTO) {
        log.debug("PUT /api/employees/{} - Updating employee", id);
        EmployeeDTO employee = employeeService.updateEmployee(id, updateDTO);
        return ResponseEntity.ok(employee);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteEmployee(@PathVariable Long id) {
        log.debug("DELETE /api/employees/{} - Deleting employee", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new MessageResponse("Employee deleted successfully"));
    }
}
