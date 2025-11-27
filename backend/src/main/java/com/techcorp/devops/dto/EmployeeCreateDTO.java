package com.techcorp.devops.dto;

import com.techcorp.devops.entity.Department;
import com.techcorp.devops.entity.Gender;
import com.techcorp.devops.entity.Level;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateDTO {
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    @NotNull(message = "Department is required")
    private Department department;
    
    @NotNull(message = "Level is required")
    private Level level;
    
    private Set<String> skills;
    
    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;
}
