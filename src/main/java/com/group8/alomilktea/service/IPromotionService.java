package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPromotionService {

    List<Promotion> findAll();

    Promotion findById(Integer id);

    void save(Promotion promotion);

    void deleteById(Integer id);

    Page<Promotion> findAll(Pageable pageable);
    Optional<Promotion> findByName(String name);
}
