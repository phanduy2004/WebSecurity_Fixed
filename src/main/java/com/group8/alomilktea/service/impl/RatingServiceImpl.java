package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.Rating;
import com.group8.alomilktea.repository.RatingRepository;
import com.group8.alomilktea.service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class RatingServiceImpl implements IRatingService {
    @Autowired
    private RatingRepository repo;

    @Override
    public List<Rating> getRatingsByProductId(Integer productId) {
        return repo.findByProductProductId(productId);
    }

    @Override
    public Rating addRating(Rating rating) {
        rating.setDate(LocalDateTime.now().toString());
        return repo.save(rating);
    }
}
