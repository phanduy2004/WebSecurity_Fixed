package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = """
        SELECT 
            p.pro_id AS productId, 
            p.name AS productName, 
            SUM(od.quantity) AS totalQuantity
        FROM 
            order_detail od
        JOIN 
            product p ON od.pro_id = p.pro_id
        GROUP BY 
            p.pro_id, p.name
        ORDER BY 
            totalQuantity DESC
        LIMIT 1
    """, nativeQuery = true)
    List<BestSellingProductDTO> findTop6BestSellingProducts();

}
