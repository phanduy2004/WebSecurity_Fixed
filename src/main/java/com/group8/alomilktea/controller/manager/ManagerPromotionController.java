package com.group8.alomilktea.controller.manager;

import com.group8.alomilktea.entity.Product;
import com.group8.alomilktea.entity.Promotion;
import com.group8.alomilktea.service.IProductService;
import com.group8.alomilktea.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/promotion")
public class ManagerPromotionController {

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IProductService productService;

    // Show promotion list
    @RequestMapping("/list")
    public String showPromotionList(ModelMap model,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {

        // Tạo đối tượng Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách promotions với phân trang
        Page<Promotion> promotionPage = promotionService.findAll(pageable);

        // Thêm dữ liệu vào model
        model.addAttribute("promotions", promotionPage.getContent());

        // Map to store Product names for each promotion
        Map<Integer, String> promotionProductMap = promotionPage.getContent().stream()
                .collect(Collectors.toMap(
                        Promotion::getPromotionId,
                        promotion -> {
                            List<Product> products = promotion.getProducts();
                            return products.stream()
                                    .map(Product::getName)
                                    .collect(Collectors.joining(", "));
                        }));

        model.addAttribute("promotionProductMap", promotionProductMap);

        // Thêm thông tin phân trang vào model
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promotionPage.getTotalPages());
        model.addAttribute("totalItems", promotionPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "manager/promotions/apps-ecommerce-promotion-list";
    }

    // Add a new promotion
    @GetMapping("/add")
    public String addPromotion(ModelMap model) {
        List<Product> products = productService.findAll(); // Get all products

        model.addAttribute("promotion", new Promotion());
        model.addAttribute("products", products);  // List of products to be chosen
        model.addAttribute("isEdit", false);

        return "manager/promotions/apps-ecommerce-promotion-create";
    }

    // Save new promotion
    @PostMapping("/add/save")
    public String savePromotion(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("discountRate") Integer discountRate,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            ModelMap model) {

        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setDescription(description);
        promotion.setDiscountRate(discountRate);

        // Get the products selected by the user
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> selectedProducts = productService.findByIds(productIds);
            promotion.setProducts(selectedProducts);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                promotion.setLogo("/uploads/" + fileName);
                imageFile.transferTo(new File("uploads/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            promotion.setLogo(imageUrl);
        }

        promotionService.save(promotion);
        model.addAttribute("message", "Promotion created successfully!");
        return "redirect:/manager/promotion/list";
    }

    // Edit an existing promotion
    @GetMapping("/edit/{promotionId}")
    public String editPromotion(@PathVariable Integer promotionId, ModelMap model) {
        Promotion promotion = promotionService.findById(promotionId); // Get promotion details
        List<Product> products = productService.findAll(); // Get list of products

        model.addAttribute("promotion", promotion);
        model.addAttribute("products", products);
        model.addAttribute("isEdit", true);

        return "manager/promotions/apps-ecommerce-promotion-create";
    }

    // Save updated promotion
    @PostMapping("/edit/save")
    public String updatePromotion(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("discountRate") Integer discountRate,
            @RequestParam(value = "productIds", required = false) List<Integer> productIds,
            ModelMap model) {

        Promotion promotion = promotionService.findById(id);
        if (promotion == null) {
            model.addAttribute("errorMessage", "Promotion not found!");
            return "redirect:/manager/promotion/list";
        }

        promotion.setName(name);
        promotion.setDescription(description);
        promotion.setDiscountRate(discountRate);

        // Get the products selected by the user
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> selectedProducts = productService.findByIds(productIds);
            promotion.setProducts(selectedProducts);
        }

        promotionService.save(promotion);
        model.addAttribute("message", "Promotion updated successfully!");
        return "redirect:/manager/promotion/list";
    }

    // Delete a promotion
    @PostMapping("/delete/{promotionId}")
    public String deletePromotion(@PathVariable("promotionId") Integer promotionId, ModelMap model) {
        Promotion promotion = promotionService.findById(promotionId);

        if (promotion == null) {
            model.addAttribute("errorMessage", "Promotion not found!");
            return "redirect:/manager/promotion/list";
        }

        promotionService.deleteById(promotionId);
        model.addAttribute("message", "Promotion deleted successfully!");
        return "redirect:/manager/promotion/list";
    }
}
