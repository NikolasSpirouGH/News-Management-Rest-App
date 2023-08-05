package com.web.restfulapi.controller;

import com.web.restfulapi.model.DepartmentEntity;
import com.web.restfulapi.model.Employee;
import com.web.restfulapi.model.EmployeeEntity;
import com.web.restfulapi.repository.DepartmentEntityRepository;
import com.web.restfulapi.repository.EmployeeEntityRepository;
import com.web.restfulapi.request.EmployeeRequest;
import com.web.restfulapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@Controller
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService eService;

    @Autowired
    private EmployeeEntityRepository eRepo;

    @Autowired
    private DepartmentEntityRepository dRepo;

    @GetMapping("/employees/all")

    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<List<Employee>>(eService.getAllEmployees(), HttpStatus.OK);
    }

    @GetMapping("/employees")

    public ResponseEntity<List<Employee>> getEmployees(@RequestParam Integer pageNumber,@RequestParam Integer pageSize) {
        return new ResponseEntity<List<Employee>>(eService.getEmployees(pageNumber,pageSize), HttpStatus.OK);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Optional<Employee>> getEmployee(@PathVariable("id") Long id) {
        return new ResponseEntity<Optional<Employee>>(eService.getSingleEmployee(id), HttpStatus.OK);
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> saveEmployee(@Valid @RequestBody Employee employee) {
        return new ResponseEntity<Employee>(eService.saveEmployee(employee), HttpStatus.CREATED);
    }

    //One To One Mapping
    @PostMapping("/employees/CreateWithDepartment")
    public ResponseEntity<EmployeeEntity> saveEmployee(@Valid @RequestBody EmployeeRequest eRequest) {
        EmployeeEntity employee = new EmployeeEntity();
        DepartmentEntity dept = new DepartmentEntity();

        dept.setName(eRequest.getDepartment());
        dept = dRepo.save(dept);

        employee.setDepartment(dept);
        employee.setName(eRequest.getName());
        employee = eRepo.save(employee);

        return new ResponseEntity<EmployeeEntity>(employee,HttpStatus.CREATED);

    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return new ResponseEntity<Employee>(eService.updateEmployee(employee), HttpStatus.OK);
    }

    @DeleteMapping("/employees")
    public ResponseEntity<HttpStatus> deleteEmployee(@RequestParam("id") Long id) {
        eService.deleteEmployee(id);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/employees/filterByName")
    public ResponseEntity<List<Employee>> getByEmployeesName(@RequestParam String name) {
        return new ResponseEntity<List<Employee>>(eService.getEmployeesByName(name), HttpStatus.OK);
    }

    @GetMapping("/employees/filterByNameAndLocation")
    public ResponseEntity<List<Employee>> getEmployeesByNameAndLocation(@RequestParam String name, @RequestParam String location) {
        return new ResponseEntity<List<Employee>>(eService.getEmployeesByNameAndLocation(name, location), HttpStatus.OK);
    }

    @GetMapping("/employees/filterByKeyword")
    public ResponseEntity<List<Employee>> getByEmployeesByKeyword(@RequestParam String name) {
        return new ResponseEntity<List<Employee>>(eService.getEmployeesByKeyword(name), HttpStatus.OK);
    }

    //Ektos tou requestparam mporoume na dokimasoume to pathvariable annotation
    @GetMapping("/employees/{name}/{location}")
    public ResponseEntity<List<Employee>> getEmployeesByNameAndLocationJDBL(@PathVariable String name, @PathVariable String location) {
        return new ResponseEntity<List<Employee>>(eService.getEmployeesByNameAndLocationJDBL(name, location), HttpStatus.OK);
    }

    @DeleteMapping ("/employees/delete/{name}")
    public ResponseEntity<String> deleteEmployeeByName(@PathVariable String name) {
        return new ResponseEntity<String>(eService.deleteEmployeeByName(name) + " Delete members", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/employees/ReadWithDepartment")
    public ResponseEntity<List<EmployeeEntity>> getEmployeesByDepartment(@RequestParam String name) {
        return new ResponseEntity<List<EmployeeEntity>>(eRepo.findByDepartmentName(name), HttpStatus.OK);
    }

    @GetMapping("/employees/ReadWithDepartmentJDBL")
    public ResponseEntity<List<EmployeeEntity>> getEmployeesByDeptJDBL(@RequestParam String name) {
        return new ResponseEntity<List<EmployeeEntity>>(eRepo.getEmployeesByDept(name), HttpStatus.OK);
    }
}