package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.Product;
import com.group8.alomilktea.entity.ProductDetail;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.repository.BestSellingProductDTO;
import com.group8.alomilktea.repository.ProductRepository;
import com.group8.alomilktea.repository.OrderDetailRepository;
import com.group8.alomilktea.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private ProductRepository repo;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public Product save(Product product) {
        return repo.save(product);
    }

    @Override
    public Product findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Optional<Product> findById0p(Integer id) {
        return repo.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public Page<Product> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Product> findAll() {
        return repo.findAll();
    }

    @Override
    public List<ProductDetail> findProductDetailsByProductId(Integer productId) {
        return repo.findProductDetailsByProductId(productId);
    }

    @Override
    public List<ProductDetailDTO> findProductInfoByID(Integer productId) {
        return repo.findProductInfoByID(productId);
    }

    @Override
    public List<ProductDetailDTO> findProductInfoBySize() {
        return repo.findProductInfoBySize();
    }

    @Override
    public ProductDetailDTO findProductInfoByIDAndSize(Long productId, String size) {
        return repo.findProductInfoByIDAndSize(productId, size);
    }

    @Override
    public ProductDetail findPriceByProductIdAndSize(Integer productId, ProductAttribute size) {
        return repo.findPriceByProductIdAndSize(productId, size);
    }

    @Override
    public List<Product> findByIds(List<Integer> ids) {
        return repo.findByProIdIn(ids);
    }

    @Override
    public Page<ProductDetailDTO> findProductInfoByCatIDPaged(Integer categoryId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return repo.findProductsByCategoryPaged(categoryId, pageable);
    }

    @Override
    public String getCategoryNameByProductId(Integer productId) {
        return repo.findCategoryNameByProductId(productId);
    }

    @Override
    public String getPromotionNameByProductId(Integer productId) {
        return repo.findPromotionNameByProductId(productId);
    }

    @Override
    public List<ProductDetailDTO> findProductsByCategoryAndPrice(Integer categoryId, Double minPrice, Double maxPrice) {
        return repo.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice);
    }

    @Override
    public List<BestSellingProductDTO> getTop6BestSellingProducts() {
        return orderDetailRepository.findTop6BestSellingProducts();
    }


}


