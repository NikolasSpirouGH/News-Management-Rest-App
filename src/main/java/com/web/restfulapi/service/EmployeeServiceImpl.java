package com.web.restfulapi.service;

import com.web.restfulapi.model.Employee;
import com.web.restfulapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository eRepository;

    @Override
    public List<Employee> getEmployees(int pageNumber, int pageSize) {
        Pageable pages = PageRequest.of(pageNumber,pageSize, Sort.Direction.DESC,"id");
        return eRepository.findAll(pages).getContent();
    }

    //Manually query JDBL
    @Override
    public List<Employee> getEmployeesByNameAndLocationJDBL(String name, String location) {
        return eRepository.getEmployeesByNameAndLocationJDBL(name,location);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return eRepository.getAllEmployees();
    }

    @Override
    public Integer deleteEmployeeByName(String name) {
        return eRepository.deleteEmployeeByName(name);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return eRepository.save(employee);
    }

    @Override
    public Optional<Employee> getSingleEmployee(Long id) {
         Optional<Employee> employee = eRepository.findById(id);
         if(employee.isPresent())
         {
             return employee;
         }
         else
         {
             throw new RuntimeException("Employee dont exist : " + id);
         }
    }

    @Override
    public void deleteEmployee(Long id) {
         eRepository.deleteById(id);
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return eRepository.save(employee);
    }

    @Override
    public List<Employee> getEmployeesByName(String name) {
        return eRepository.findByName(name);
    }

    @Override
    public List<Employee> getEmployeesByNameAndLocation(String name, String location) {
        return eRepository.findByNameAndLocation(name,location);
    }

    @Override
    public List<Employee> getEmployeesByKeyword(String name) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        return eRepository.findByNameContaining(name,sort);
    }

}
