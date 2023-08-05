package com.web.restfulapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Email;

import java.util.Date;

@Setter
@Getter
@ToString
@Entity
@Table(name="tbl_employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @NotBlank(message="Name should not be null")
    @Column(name="name")
    private String name;

    @Column(name="age")
    private Long age = 0L;

    @Column(name="location")
    private String location;

    @Email(message="Please enter the valid email address")
    @Column(name="email")
    private String email;

    @NotBlank(message="Department should not be null")
    @Column(name="department")
    private String department;

    @CreationTimestamp
    @Column(name="create_at",nullable = false,updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;

}
