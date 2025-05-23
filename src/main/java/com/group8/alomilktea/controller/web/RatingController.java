package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.Product;
import com.group8.alomilktea.entity.Rating;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.ICartService;
import com.group8.alomilktea.service.IProductService;
import com.group8.alomilktea.service.IRatingService;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/api/ratings")
@RestController
public class RatingController {

    @Autowired
    IRatingService ratingService;
    @Autowired
    IUserService userService;
    @Autowired
    IProductService productService ;


    @GetMapping("/product/{productId}")
    public List<Map<String, Object>> getRatingsByProduct(@PathVariable Integer productId) {

        System.out.println("Fetching ratings for product ID: " + productId); // Log productId
        List<Rating> ratings = ratingService.getRatingsByProductId(productId);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        List<Map<String, Object>> ratingResponses = new ArrayList<>();
        if (ratings != null && !ratings.isEmpty()) {
            for (Rating rating : ratings) {
                Map<String, Object> ratingInfo = new HashMap<>();
                ratingInfo.put("userName", rating.getUser().getFullName());
                ratingInfo.put("rate", rating.getRate());
                ratingInfo.put("content", rating.getContent());
                String dateStr = rating.getDate();
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        Date date = inputFormat.parse(dateStr);
                        String formattedDate = outputFormat.format(date);
                        ratingInfo.put("date", formattedDate);
                    } catch (ParseException e) {
                        System.err.println("Error parsing date: " + dateStr);
                        e.printStackTrace();
                        ratingInfo.put("date", dateStr); // Nếu lỗi, trả về ngày gốc
                    }
                }
                ratingResponses.add(ratingInfo);
            }
        } else {
            System.out.println("No ratings found for product ID: " + productId);
        }
        return ratingResponses;
    }
    @PostMapping("/add")
    public String addRating(
            @RequestParam Integer productId,
            @RequestParam String content,
            @RequestParam Integer rate,
            @RequestParam String platform,
            @RequestParam String date
    ) {
        Rating rating = new Rating();
        User userLogged = userService.getUserLogged();
        rating.setUser(userLogged);
        Product product = productService.findById(productId);
        rating.setProduct(product);
        rating.setContent(content);
        rating.setRate(rate);
        rating.setPlatform(platform);
        rating.setDate(date);
        Rating newRating = ratingService.addRating(rating); // Lưu đánh giá vào database
        System.out.println("Rating added: " + newRating); // Log thông tin đánh giá mới đã được thêm
        return "Lưu thành công";
    }
}
