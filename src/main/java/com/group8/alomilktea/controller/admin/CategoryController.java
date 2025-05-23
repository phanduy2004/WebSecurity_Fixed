package com.group8.alomilktea.controller.admin;

import com.group8.alomilktea.entity.Category;
import com.group8.alomilktea.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("/list")
    public String showCategoryList(ModelMap model,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<Category> categoryPage = categoryService.getAll(PageRequest.of(page, size));

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());

        model.addAttribute("pageSize", size);

        return "admin/categories/apps-ecommerce-category-list";
    }


    @GetMapping("/add")
    public String addCategory(ModelMap model) {
        model.addAttribute("category", new Category());
        model.addAttribute("isEdit", false);
        return "admin/categories/apps-ecommerce-category-create";
    }

    @PostMapping("/add/save")
    public String saveCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "description", required = false) String description,
            ModelMap model) {

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                category.setLogo("/uploads/" + fileName);
                imageFile.transferTo(new File("uploads/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            category.setLogo(imageUrl);
        }

        categoryService.save(category);
        model.addAttribute("message", "Category created successfully!");
        return "redirect:/admin/category/list";
    }

    @GetMapping("/edit/{cateId}")
    public String editCategory(@PathVariable Integer cateId, ModelMap model) {
        Category category = categoryService.findById(cateId);
        if (category == null) {
            model.addAttribute("errorMessage", "Category not found!");
            return "redirect:/admin/category/list";
        }
        model.addAttribute("category", category);
        model.addAttribute("isEdit", true);
        return "admin/categories/apps-ecommerce-category-create";
    }


    @PostMapping("/edit/save")
    public String updateCategory(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "description", required = false) String description,
            ModelMap model) {

        Category category = categoryService.findById(id);
        if (category == null) {
            model.addAttribute("errorMessage", "Category not found!");
            return "redirect:/admin/category/list";
        }

        category.setName(name);
        category.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                category.setLogo("/uploads/" + fileName);
                imageFile.transferTo(new File("uploads/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            category.setLogo(imageUrl);
        }

        categoryService.save(category);
        model.addAttribute("message", "Category updated successfully!");
        return "redirect:/admin/category/list";
    }

    @PostMapping("/delete/{cateId}")
    public String deleteCategory(@PathVariable("cateId") Integer cateId, ModelMap model){
        Category category = categoryService.findById(cateId);

        if (category == null){
            model.addAttribute("errorMessage", "Category not found!");
            return "redirect:/admin/category/list";
        }
        else
            categoryService.deleteById(cateId);

        model.addAttribute("message","Delete success");
        return "redirect:/admin/category/list";
    }

}
