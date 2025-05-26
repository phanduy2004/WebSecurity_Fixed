package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.Category;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.service.ICategoryService;
import com.group8.alomilktea.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = {"web/category"})  // Cố định id = 1
@Controller("webCategoryController")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IProductService productService ;

    @GetMapping("{id}")
    public String showCategories(@PathVariable("id") Integer id,
                                 @RequestParam(defaultValue = "1") int pageNo,
                                 @RequestParam(defaultValue = "6") int pageSize,
                                 ModelMap model) {
        Page<ProductDetailDTO> page = productService.findProductInfoByCatIDPaged(id, pageNo, pageSize);
        String namec = categoryService.findNameById(id);
        List<Category> listcat = categoryService.findAll();

        model.addAttribute("products", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", listcat);
        model.addAttribute("name", namec);
        model.addAttribute("idcate", id);
        return "web/billy/category";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<List<ProductDetailDTO>> getProductsByCategoryAndPrice(
            @PathVariable("id") Integer id,
            @RequestParam("minPrice") Double minPrice,
            @RequestParam("maxPrice") Double maxPrice) {
        List<ProductDetailDTO> products = productService.findProductsByCategoryAndPrice(id, minPrice, maxPrice);
        // In ra kết quả trả về để kiểm tra
        System.out.println("Returned Products: ");
        products.forEach(product -> System.out.println("Product ID: " + product.getProId() + ", Name: " + product.getName() + ", Price: " + product.getPrice()));
        return ResponseEntity.ok(products);
    }
}
