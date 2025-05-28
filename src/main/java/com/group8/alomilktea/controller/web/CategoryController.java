package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.Category;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.service.ICategoryService;
import com.group8.alomilktea.service.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequestMapping(value = {"web/category"})
@Controller("webCategoryController")
public class CategoryController {

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IProductService productService;

    @GetMapping("{id}")
    public String showCategories(@PathVariable("id") Integer id,
                                 @RequestParam(defaultValue = "1") int pageNo,
                                 @RequestParam(defaultValue = "6") int pageSize,
                                 ModelMap model) {

        if (pageNo < 1) {
            model.addAttribute("errorMessage", "Số trang không hợp lệ.");
            model.addAttribute("errorDetails", "Số trang phải lớn hơn hoặc bằng 1. Bạn đã yêu cầu trang: " + pageNo);
            return "error/custom_error";
        }

        Page<ProductDetailDTO> page;
        try {
            page = productService.findProductInfoByCatIDPaged(id, pageNo, pageSize);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Số trang không hợp lệ sau khi xử lý.");
            model.addAttribute("errorDetails", e.getMessage());
            return "error/custom_error";
        }

        String namec = categoryService.findNameById(id);

        if (namec == null) {
            model.addAttribute("errorMessage", "Không tìm thấy danh mục với ID " + id);
            return "error/custom_error";
        }

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

        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return ResponseEntity.badRequest().body(null);
        }
        List<ProductDetailDTO> products = productService.findProductsByCategoryAndPrice(id, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String invalidValue = (ex.getValue() != null) ? ex.getValue().toString() : "null";
        String parameterName = ex.getName();
        String requiredType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "N/A";

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Dữ liệu đầu vào không hợp lệ.");
        mav.addObject("errorDetails", "Giá trị '" + invalidValue + "' không hợp lệ cho tham số '" + parameterName + "'. Yêu cầu kiểu '" + requiredType + "'.");
        mav.setViewName("error/custom_error");
        return mav;
    }
}