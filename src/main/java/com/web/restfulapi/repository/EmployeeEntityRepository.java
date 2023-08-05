package com.web.restfulapi.repository;

import com.web.restfulapi.model.Employee;
import com.web.restfulapi.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeEntityRepository extends JpaRepository<EmployeeEntity,Long> {

    List<EmployeeEntity> findByDepartmentName(String name);

    //Tha dokimsasoume to idio me thn findByDepartmentName alla me mannualy JDBL QUERY
    @Query("FROM EmployeeEntity WHERE department.name= :name")
    List<EmployeeEntity> getEmployeesByDept(String name);

}
