package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.CartKey;
import com.group8.alomilktea.entity.Wishlist;
import com.group8.alomilktea.repository.WishlistRepository;
import com.group8.alomilktea.service.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {
    @Autowired
    WishlistRepository wishlistRepository;

    public <S extends Wishlist> S save(S entity) {
        return wishlistRepository.save(entity);
    }

    @Override
    public Optional<Wishlist> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public void deleteById(CartKey id) {
        wishlistRepository.deleteById(id);
    }

    @Override
    public List<Wishlist> findByUserId(Integer userId) {
        return wishlistRepository.findByUserId(userId);
    }
}
