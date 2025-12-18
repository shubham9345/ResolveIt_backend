package com.org.ResolveIt.service;

import com.org.ResolveIt.Exception.UserNotFoundException;
import com.org.ResolveIt.model.CategoryType;
import com.org.ResolveIt.model.Employee;
import com.org.ResolveIt.model.UserInfo;
import com.org.ResolveIt.repository.EmployeeRepository;
import com.org.ResolveIt.repository.UserInfoRepository;
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

    public Employee addEmployeeByUserId(Employee employee, Long userId){
        Optional<UserInfo> userInfo = userInfoRepository.findById(userId);
        if(userInfo.isEmpty()){
            throw new UserNotFoundException("user not found with this UserId", "user not found with this userId");
        }
        userInfo.get().setRoles("EMPLOYEE");
        employee.setUser(userInfo.get());
        employee.setJoinDate(LocalDate.now());

        employeeRepository.save(employee);
        return employee;
    }
    public List<Employee> allEmployee(){
        return employeeRepository.findAll();
    }
    public List<Employee> getCategoryType(CategoryType categoryType){
        return employeeRepository.findByCategoryType(categoryType);
    }
}
