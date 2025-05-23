package com.group8.alomilktea.controller.manager;

import com.group8.alomilktea.repository.BestSellingProductDTO;
import com.group8.alomilktea.service.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductControllerTop {
    @Autowired
    private ProductService productService;

    @GetMapping("/top-best-selling")
    public ResponseEntity<List<BestSellingProductDTO>> getTopBestSellingProducts() {
        List<BestSellingProductDTO> products = productService.getTop6BestSellingProducts();
        return ResponseEntity.ok(products);
    }
}
