package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.common.enums.ProductAttribute; // Đảm bảo import enum này
import com.group8.alomilktea.entity.ProductDetail;
import com.group8.alomilktea.repository.ProductDetailRepository;
import com.group8.alomilktea.service.IProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductDetailServiceImpl implements IProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailRepository; // Đổi tên repo cho nhất quán

    @Override
    public ProductDetail findById(Integer id) {
        return productDetailRepository.findById(id).orElse(null);
    }

    @Override
    public ProductDetail save(ProductDetail productDetail) {
        return productDetailRepository.save(productDetail);
    }

    @Override
    public ProductDetail findPriceByProductIdAndSize(Integer productId, ProductAttribute size) {
        Optional<ProductDetail> productDetailOptional = productDetailRepository.findByProduct_ProIdAndSize(productId, size);
        return productDetailOptional.orElse(null);
    }
}