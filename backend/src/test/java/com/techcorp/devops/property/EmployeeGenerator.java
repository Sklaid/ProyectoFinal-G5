package com.techcorp.devops.property;

import com.techcorp.devops.dto.EmployeeCreateDTO;
import com.techcorp.devops.entity.Department;
import com.techcorp.devops.entity.Gender;
import com.techcorp.devops.entity.Level;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class EmployeeGenerator {
    
    public static Arbitrary<EmployeeCreateDTO> validEmployees() {
        Arbitrary<String> firstNames = Arbitraries.strings()
                .alpha()
                .ofMinLength(2)
                .ofMaxLength(50);
        
        Arbitrary<String> lastNames = Arbitraries.strings()
                .alpha()
                .ofMinLength(2)
                .ofMaxLength(50);
        
        // Add timestamp to ensure email uniqueness across test runs
        Arbitrary<String> emails = Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(3)
                .ofMaxLength(15)
                .map(s -> s + System.nanoTime() + "@example.com");
        
        Arbitrary<String> phones = Arbitraries.strings()
                .numeric()
                .ofMinLength(10)
                .ofMaxLength(15)
                .map(s -> "+1" + s);
        
        Arbitrary<Gender> genders = Arbitraries.of(Gender.class);
        
        Arbitrary<Department> departments = Arbitraries.of(Department.class);
        
        Arbitrary<Level> levels = Arbitraries.of(Level.class);
        
        Arbitrary<Set<String>> skills = Arbitraries.of("Java", "Python", "React", "Docker", "Kubernetes", "AWS")
                .set()
                .ofMinSize(0)
                .ofMaxSize(4);
        
        Arbitrary<LocalDate> hireDates = Arbitraries.integers()
                .between(0, 3650) // Last 10 years
                .map(days -> LocalDate.now().minusDays(days));
        
        // Combine in two steps to avoid the 8-parameter limit
        return Combinators.combine(
                firstNames,
                lastNames,
                emails,
                phones
        ).as((firstName, lastName, email, phone) ->
                Combinators.combine(
                        genders,
                        departments,
                        levels,
                        skills,
                        hireDates
                ).as((gender, department, level, skillSet, hireDate) ->
                        EmployeeCreateDTO.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .phone(phone)
                                .gender(gender)
                                .department(department)
                                .level(level)
                                .skills(new HashSet<>(skillSet))
                                .hireDate(hireDate)
                                .build()
                )
        ).flatMap(arb -> arb);
    }
    
    public static Arbitrary<EmployeeCreateDTO> invalidEmailEmployees() {
        return validEmployees().map(dto -> {
            // Generate various invalid email formats
            String invalidEmail = Arbitraries.of(
                    "notanemail",
                    "@example.com",
                    "user@",
                    "user @example.com",
                    "user@.com",
                    ""
            ).sample();
            dto.setEmail(invalidEmail);
            return dto;
        });
    }
}
