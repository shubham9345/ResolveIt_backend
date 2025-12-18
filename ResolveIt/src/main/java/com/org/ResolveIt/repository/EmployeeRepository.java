package com.org.ResolveIt.repository;

import com.org.ResolveIt.model.CategoryType;
import com.org.ResolveIt.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    List<Employee> findByCategoryType(CategoryType categoryType);
}
