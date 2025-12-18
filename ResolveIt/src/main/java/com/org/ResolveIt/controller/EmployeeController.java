package com.org.ResolveIt.controller;

import com.org.ResolveIt.model.CategoryType;
import com.org.ResolveIt.model.CommentEmployee;
import com.org.ResolveIt.model.Complaints;
import com.org.ResolveIt.model.Employee;
import com.org.ResolveIt.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    @GetMapping("/employee-category")
    public List<Employee> allEmployeeByCategoryType(@RequestParam CategoryType categoryType){
        return employeeService.getCategoryType(categoryType);
    }
    @PostMapping("/assigned-complaints/{complaintsId}/{empId}")
    public String assignedComplaint(@PathVariable Long complaintsId, @PathVariable Long empId){
        return employeeService.assignedComplaint(complaintsId,empId);
    }
    @GetMapping("/all-complaints/{empId}")
    public List<Complaints> allComplaintsByEmpId(@PathVariable Long empId){
        return employeeService.complaintsByEmployeeId(empId);
    }
    @GetMapping("/{userId}")
    public Employee employeeByEmpId(@PathVariable Long userId){
        return employeeService.employeeByUserId(userId);
    }
    @PostMapping("/comment-employee/{complaintId}/{empId}")
    public String commentEmployee(@PathVariable Long complaintId, @PathVariable Long empId, @RequestParam String desc){
        return employeeService.employeeComment(complaintId,empId,desc);
    }
    @GetMapping("/all-comment/{empId}")
    public List<CommentEmployee> allComments(@PathVariable Long empId){
        return employeeService.allCommentToEmployee(empId);
    }
}
