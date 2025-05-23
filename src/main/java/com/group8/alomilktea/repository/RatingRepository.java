package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    @Query("SELECT r FROM Rating r WHERE r.product.proId = :productId")
    List<Rating> findByProductProductId(Integer productId);
}
