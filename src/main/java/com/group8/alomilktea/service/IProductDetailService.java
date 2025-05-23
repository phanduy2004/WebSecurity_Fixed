package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.ProductDetail;

public interface IProductDetailService {
    ProductDetail findById(Integer id);

    ProductDetail save(ProductDetail productDetail);
}
