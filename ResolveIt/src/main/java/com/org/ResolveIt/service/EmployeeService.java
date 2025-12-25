package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.*;
import com.org.ResolveIt.repository.CommentEmployeeRepository;
import com.org.ResolveIt.repository.ComplaintsRepository;
import com.org.ResolveIt.repository.EmployeeRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ComplaintsRepository complaintsRepository;
    @Autowired
    private CommentEmployeeRepository commentEmployeeRepository;

    @Transactional
    public Employee addEmployeeByUserId(Employee employee, Long userId) {
        Optional<UserInfo> userInfo = userInfoRepository.findById(userId);
        if (userInfo.isEmpty()) {
            throw new UserNotFoundException("user not found with this UserId", "user not found with this userId");
        }
        if (employeeRepository.existsById(userId)) {
            throw new IllegalStateException("Employee already exists for this user");
        }
        userInfo.get().setRoles("EMPLOYEE");
        employee.setUser(userInfo.get());
        employee.setJoinDate(LocalDate.now());

        employeeRepository.save(employee);
        return employee;
    }

    public List<Employee> allEmployee() {
        return employeeRepository.findAll();
    }

    public List<Employee> getCategoryType(CategoryType categoryType) {
        return employeeRepository.findByCategoryType(categoryType);
    }

    @Transactional
    public String assignedComplaint(Long complaintId, Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "employee not found",
                                "employee is not found with this Id"
                        )
                );

        Complaints complaint = complaintsRepository.findById(complaintId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "complaint not found",
                                "complaint is not found with this Id"
                        )
                );


        complaint.setAssignedEmployee(employee);
        complaint.setAssigned(true);

        employee.getAllComplaints().add(complaint);

        complaintsRepository.save(complaint);

        return "Complaint successfully assigned!";
    }

    public List<Complaints> complaintsByEmployeeId(Long EmployeeId) {
        Optional<Employee> employee = employeeRepository.findById(EmployeeId);
        if (employee.isEmpty()) {
            throw new UserNotFoundException("emp Id not exist", "employee is not found with this empId");
        }
        return employee.get().getAllComplaints();
    }

    public Employee employeeByUserId(Long userId) {
        Optional<UserInfo> employee = userInfoRepository.findById(userId);
        if (employee.isEmpty()) {
            throw new UserNotFoundException("empId does not exist", "employee do not exist with this empId");
        }
        return employee.get().getEmployee();

    }

    public String employeeComment(Long complaintId, Long empId, String desc) {

        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Complaints complaint = complaintsRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        CommentEmployee commentEmployee = new CommentEmployee();
        commentEmployee.setComment(desc);
        commentEmployee.setEmployee(employee);
        commentEmployee.setCommentDate(LocalDate.now());
        commentEmployee.setComplaintEmployee(complaint);
        employee.getCommentEmployeeList().add(commentEmployee);
        complaint.getCommentsEmployee().add(commentEmployee);
        commentEmployeeRepository.save(commentEmployee);
        return "Comment added successfully!!";
    }

    public List<CommentEmployee> allCommentToEmployee(Long empId) {
        Optional<Employee> employee = employeeRepository.findById(empId);
        if (employee.isEmpty()) {
            throw new UserNotFoundException("empId does not exist", "employee is not found with this empId");
        }
        return employee.get().getCommentEmployeeList();
    }

}
