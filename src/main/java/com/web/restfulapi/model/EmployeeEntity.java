package com.web.restfulapi.model;

import com.web.restfulapi.request.EmployeeRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name="tbl_employee_entity")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column
    private String name;

    @JoinColumn(name="department_id")
    @OneToOne
    private DepartmentEntity department;

}
