package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
}
