package com.group8.alomilktea.service;

import com.group8.alomilktea.common.enums.ProductAttribute; // Đảm bảo import enum này
import com.group8.alomilktea.entity.ProductDetail;

public interface IProductDetailService {
    ProductDetail findById(Integer id);
    ProductDetail save(ProductDetail productDetail);

    // Phương thức mới cần thêm
    ProductDetail findPriceByProductIdAndSize(Integer productId, ProductAttribute size);
}