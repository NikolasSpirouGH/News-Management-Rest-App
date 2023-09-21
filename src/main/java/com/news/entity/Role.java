package com.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id)")
    private Long id;

    @Column(name = "name")
    private String name;

    public Role(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}