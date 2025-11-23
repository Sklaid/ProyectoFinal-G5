package com.techcorp.devops.repository;

import com.techcorp.devops.entity.Department;
import com.techcorp.devops.entity.Employee;
import com.techcorp.devops.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Employee> findByDepartment(Department department);
    
    List<Employee> findByLevel(Level level);
    
    List<Employee> findByDepartmentAndLevel(Department department, Level level);
}
