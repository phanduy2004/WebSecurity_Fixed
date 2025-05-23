package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c ORDER BY c.cateId ASC")
    List<Category> findTop10();

    Page<Category> findAll(Pageable pageable);

    @Query("SELECT c.name FROM Category c WHERE c.cateId = :id")
    String findNameById(@Param("id") Integer id);
}
