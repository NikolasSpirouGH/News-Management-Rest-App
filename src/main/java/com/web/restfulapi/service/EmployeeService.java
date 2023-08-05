package com.web.restfulapi.service;

import com.web.restfulapi.model.Employee;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);

    Optional<Employee> getSingleEmployee(Long id);

    void deleteEmployee(Long id);

    Employee updateEmployee(Employee employee);

    List<Employee> getEmployeesByName(String name);

    List<Employee> getEmployeesByNameAndLocation(String name, String location);

    List<Employee> getEmployeesByKeyword(String name);

    List<Employee> getEmployees(int pageNumber, int pageSize);

    List<Employee> getEmployeesByNameAndLocationJDBL(String name, String location);

    List<Employee> getAllEmployees();

    Integer deleteEmployeeByName(String name);
}
