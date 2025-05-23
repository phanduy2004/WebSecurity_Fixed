package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.CartKey;
import com.group8.alomilktea.entity.Wishlist;

import java.util.List;
import java.util.Optional;

public interface IWishlistService {
    List<Wishlist> findByUserId(Integer userId);
    <S extends Wishlist> S save(S entity);
    Optional<Wishlist> findById(Integer integer);
    void deleteById(CartKey id);
}
