package com.example.project_noah_ritcey.users;

import com.example.project_noah_ritcey.entities.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EmployeeUserDetails {
    private final Employee employee;

    public EmployeeUserDetails(Employee employee) {
        this.employee = employee;
    }
    public Employee getEmployee() {
        return employee;
    }
    public Integer getEmployeeId() {
        return employee.getId();
    }
}
