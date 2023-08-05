package com.web.restfulapi.repository;

import com.web.restfulapi.model.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentEntityRepository extends JpaRepository<DepartmentEntity, Long> {
}
