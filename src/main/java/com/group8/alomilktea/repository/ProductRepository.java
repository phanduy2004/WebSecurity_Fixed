package com.group8.alomilktea.repository;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.Product;
import com.group8.alomilktea.entity.ProductDetail;
import com.group8.alomilktea.model.ProductDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p.category.name FROM Product p WHERE p.proId = :productId")
    String findCategoryNameByProductId(@Param("productId") Integer productId);

    @Query("SELECT p.promotion.name FROM Product p WHERE p.proId = :productId")
    String findPromotionNameByProductId(@Param("productId") Integer productId);

    @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.proId = :productId")
    List<ProductDetail> findProductDetailsByProductId(@Param("productId") Integer productId);
    @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p " +
            "LEFT JOIN p.productDetails pd " +
            "WHERE pd.size = 'M'")
    List<ProductDetailDTO> findProductInfoBySize();
    @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p " +
            "LEFT JOIN p.productDetails pd " +
            "WHERE pd.size = 'M' AND p.proId = :productId")
    List<ProductDetailDTO> findProductInfoByID(@Param("productId") Integer productId);
    @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p " +
            "LEFT JOIN ProductDetail pd " +
            "WHERE p.proId = :productId AND pd.size = :size")
    ProductDetailDTO findProductInfoByIDAndSize(@Param("productId") Long productId, @Param("size") String size);

    @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.proId = :productId AND pd.size = :size")
    ProductDetail findPriceByProductIdAndSize(@Param("productId") Integer productId, @Param("size") ProductAttribute size);

    List<Product> findByProIdIn(List<Integer> ids);
   @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p " +
            "LEFT JOIN p.productDetails pd " +
            "WHERE pd.size = 'M' AND p.category.cateId = :catId")
    List<ProductDetailDTO> findProductInfoByCatID(@Param("catId") Integer catId);

    @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p " +
            "LEFT JOIN p.productDetails pd " +
            "WHERE pd.size = 'M' AND p.category.cateId = :categoryId AND pd.price BETWEEN :minPrice AND :maxPrice ORDER BY pd.price ASC")
    List<ProductDetailDTO> findByCategoryAndPriceRange(
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);


    @Query("SELECT new com.group8.alomilktea.model.ProductDetailDTO( " +
            "p.proId, p.name, p.description, p.imageLink, p.category.cateId, " +
            "pd.proDId, pd.size, pd.price) " +
            "FROM Product p LEFT JOIN p.productDetails pd " +
            "WHERE pd.size = 'M' AND p.category.cateId = :categoryId")
    Page<ProductDetailDTO> findProductsByCategoryPaged(@Param("categoryId") Integer categoryId, Pageable pageable);



}
