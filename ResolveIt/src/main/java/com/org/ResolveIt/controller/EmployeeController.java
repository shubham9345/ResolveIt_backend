package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.CategoryType;
import com.org.ResolveIt.model.Employee;
import com.org.ResolveIt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/add-employee/{userId}")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee, @PathVariable Long userId){
      Employee newEmployee = employeeService.addEmployeeByUserId(employee,userId);
      return new ResponseEntity<>(newEmployee, HttpStatus.OK);
    }
    @GetMapping("/all-employee")
    public List<Employee> allEmployee(){
        return employeeService.allEmployee();
    }
    @GetMapping("/api/employee-category")
    public List<Employee> allEmployeeByCategoryType(@RequestParam CategoryType categoryType){
        return employeeService.getCategoryType(categoryType);
    }
}
