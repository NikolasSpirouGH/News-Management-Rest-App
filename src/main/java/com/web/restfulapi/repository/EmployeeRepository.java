package com.web.restfulapi.repository;

import com.web.restfulapi.model.Employee;
import jakarta.transaction.Transactional;
import org.hibernate.sql.Delete;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long>, PagingAndSortingRepository<Employee,Long> {

    List<Employee> findByName(String name);

    List<Employee> findByNameAndLocation(String name, String Location);

    List<Employee> findByNameContaining(String keyword, Sort sort);

    //Antitheta me tis parapanw edw tha kanoyme mannualy jdbl query. To name ths synartisis den exei kamia simasia opws ta finders apo panw.
    //An ta vars den itan name kai location opws stin  db, tha prepe na bazame [ @param(name) String value kai @Param(location) String value ].
    @Query("FROM Employee where name= :name AND location= :location")
    List<Employee> getEmployeesByNameAndLocationJDBL(String name, String location);

    @Query("FROM Employee")
    List<Employee> getAllEmployees();

    @Transactional
    @Modifying
    @Query("DELETE FROM Employee where name= :name")
    Integer deleteEmployeeByName(String name);
}
