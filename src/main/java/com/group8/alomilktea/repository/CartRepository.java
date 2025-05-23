package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Cart;
import com.group8.alomilktea.entity.CartKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartKey> {
    @Query(value = "select * from cart where user_id=?", nativeQuery = true)
    List<Cart> findByUserId(Integer userId);

    @Query(value = "select * from cart where user_id=? and pro_id=?", nativeQuery = true)
    List<Cart> findByUserIdAndProid(Integer userId, Integer proId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.userId = :userId")
    void clearCart(@Param("userId") Integer userId);

}

