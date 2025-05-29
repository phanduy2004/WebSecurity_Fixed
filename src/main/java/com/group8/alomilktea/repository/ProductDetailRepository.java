package com.group8.alomilktea.repository;

import com.group8.alomilktea.common.enums.ProductAttribute; // Đảm bảo import enum này
import com.group8.alomilktea.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Thêm nếu dùng @Query
import org.springframework.data.repository.query.Param; // Thêm nếu dùng @Query
import org.springframework.stereotype.Repository;

import java.util.Optional; // Nên sử dụng Optional để xử lý null tốt hơn

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    Optional<ProductDetail> findByProduct_ProIdAndSize(Integer productId, ProductAttribute size);
}