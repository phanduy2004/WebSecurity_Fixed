package com.group8.alomilktea.service;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.Product;
import com.group8.alomilktea.entity.ProductDetail;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.repository.BestSellingProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    Product save(Product product);

    Product findById(Integer id);
    Optional<Product> findById0p(Integer id);
    void deleteById(Integer id);

    Page<Product> getAll(Pageable pageable);

    List<Product> findAll();
    String getCategoryNameByProductId(Integer productId);

    String getPromotionNameByProductId(Integer productId);

    List<ProductDetail> findProductDetailsByProductId(Integer productId);

    List<ProductDetailDTO> findProductInfoByID(Integer productId);

    List<ProductDetailDTO> findProductInfoBySize();

    ProductDetailDTO findProductInfoByIDAndSize(Long productId, String size);

    ProductDetail findPriceByProductIdAndSize( Integer productId, ProductAttribute size);

    List<Product> findByIds(List<Integer> ids);

    Page<ProductDetailDTO> findProductInfoByCatIDPaged(Integer categoryId, int pageNo, int pageSize);

    List<ProductDetailDTO> findProductsByCategoryAndPrice(Integer categoryId, Double minPrice, Double maxPrice);

    List<BestSellingProductDTO> getTop6BestSellingProducts();
}