package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.ProductDetail;
import com.group8.alomilktea.repository.ProductDetailRepository;
import com.group8.alomilktea.service.IProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductDetailServiceImpl implements IProductDetailService {

    @Autowired
    private ProductDetailRepository repo;

    @Override
    public ProductDetail findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public ProductDetail save(ProductDetail productDetail) {
        return repo.save(productDetail); // Trả về đối tượng sau khi lưu
    }
}
