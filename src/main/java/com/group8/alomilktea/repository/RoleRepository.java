package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Roles, Integer> {

    // Lấy tất cả các role
    List<Roles> findAll();

    // Lấy danh sách role theo danh sách ID
    Set<Roles> findByIdIn(List<Integer> ids);
}
