package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Rating;

import java.util.List;

public interface IRatingService {

     List<Rating> getRatingsByProductId(Integer productId);
     Rating addRating(Rating rating);
}
