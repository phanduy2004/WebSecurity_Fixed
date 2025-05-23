package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICategoryService {
    List<Category> findTop10();

    Page<Category> getAll(Pageable pageable);

    void deleteById(Integer id);

    long count();

    Category findById(Integer id);

    List<Category> findAllById(Iterable<Integer> id);

    List<Category> findAll();

    <S extends Category> S save(S entity);

    String findNameById(Integer id);
}
