package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Cart;
import com.group8.alomilktea.entity.CartKey;
import com.group8.alomilktea.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, CartKey> {
    @Query(value = "select * from wishlist where user_id=?", nativeQuery = true)
    List<Wishlist> findByUserId(Integer userId);
}
