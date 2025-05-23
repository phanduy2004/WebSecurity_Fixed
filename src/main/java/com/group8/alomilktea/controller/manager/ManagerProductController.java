    package com.group8.alomilktea.controller.manager;

    import com.group8.alomilktea.common.enums.ProductAttribute;
    import com.group8.alomilktea.entity.Category;
    import com.group8.alomilktea.entity.Product;
    import com.group8.alomilktea.entity.ProductDetail;
    import com.group8.alomilktea.entity.Promotion;
    import com.group8.alomilktea.repository.BestSellingProductDTO;
    import com.group8.alomilktea.service.ICategoryService;
    import com.group8.alomilktea.service.IProductDetailService;
    import com.group8.alomilktea.service.IProductService;
    import com.group8.alomilktea.service.IPromotionService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
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
    @RequestMapping("/manager/product")
    public class ManagerProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IProductDetailService productDetailService;

    @RequestMapping("/list")
    public String showProductList(ModelMap model,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size) {

    Page<Product> productPage = productService.getAll(PageRequest.of(page, size));
    List<Product> products = productPage.getContent();

    Map<Integer, String> productCategoryMap = products.stream()
            .collect(Collectors.toMap(
                    Product::getProId,
                    product -> {
                        String categoryName = productService.getCategoryNameByProductId(product.getProId());
                        return categoryName != null ? categoryName : "No Category";
                    }));

    Map<Integer, String> productPromotionMap = products.stream()
            .collect(Collectors.toMap(
                    Product::getProId,
                    product -> {
                        String promotionName = productService.getPromotionNameByProductId(product.getProId());
                        return promotionName != null ? promotionName : "No Promotion";
                    }));

    model.addAttribute("products", products);
    model.addAttribute("productCategoryMap", productCategoryMap);
    model.addAttribute("productPromotionMap", productPromotionMap);

    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalItems", productPage.getTotalElements());
    model.addAttribute("pageSize", size);

    return "manager/products/apps-ecommerce-product-list";
    }

    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Integer id, Model model) {
    Product product = productService.findById(id);
    // Truyền list ProductDetail vào model
    model.addAttribute("productdetail", product.getProductDetails());
    return "manager/products/apps-ecommerce-product-detail-list";
    }

    @GetMapping("/details/edit/{prodid}")
    public String editProductDetail(@PathVariable Integer prodid, ModelMap model) {
        ProductDetail productDetail =productDetailService .findById(prodid); // Lấy thông tin sản phẩm

        model.addAttribute("productDetail", productDetail);
        return "manager/products/apps-ecommerce-product-detail-edit";
    }

    @PostMapping("/details/edit/save")
    public String saveProductDetail(@RequestParam("prodId") Integer prodId,
                                    @RequestParam("price") Double price, ModelMap model){
        ProductDetail productDetail = productDetailService.findById(prodId);

        Integer productId = productDetail.getProduct().getProId();

        if (productDetail == null) {
            model.addAttribute("errorMessage", "Product not found!");
            return "redirect:/manager/product/detail";
        }
        if (price == null || price <= 0) {
            model.addAttribute("errorMessage", "Giá không hợp lệ!");
            return "redirect:/manager/product/details/edit/" + prodId;
        }
        productDetail.setPrice(price);
        productDetailService.save(productDetail); // Lưu cập nhật
        return "redirect:/manager/product/details/" + productId;
    }
    @GetMapping("/add")
    public String addProduct(ModelMap model) {
    // Lấy tất cả các category và promotion từ database
    List<Category> categories = categoryService.findAll();
    List<Promotion> promotions = promotionService.findAll();

    // Thêm vào model để hiển thị trong combobox
    model.addAttribute("product", new Product());
    model.addAttribute("categories", categories);  // Danh sách categories
    model.addAttribute("promotions", promotions);  // Danh sách promotions
    model.addAttribute("isEdit", false);

    return "manager/products/apps-ecommerce-product-create";
    }

    @PostMapping("/add/save")
    public String saveProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "promotionId", required = false) Integer promotionId,
            @RequestParam("categoryId") Integer categoryId,
            ModelMap model) {

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);

        // Lấy đối tượng Promotion và Category
        Promotion promotion = (promotionId != null) ? promotionService.findById(promotionId) : null;
        Category category = (categoryId != null) ? categoryService.findById(categoryId) : null;

        product.setPromotion(promotion);
        product.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                product.setImageLink("/uploads/" + fileName);
                imageFile.transferTo(new File("uploads/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            product.setImageLink(imageUrl);
        }

        // Tạo danh sách ProductDetail
        List<ProductDetail> productDetails = List.of(
                new ProductDetail(null, ProductAttribute.S, null, product),
                new ProductDetail(null, ProductAttribute.M, null, product),
                new ProductDetail(null, ProductAttribute.L, null, product)
        );

        product.setProductDetails(productDetails);

        // Lưu Product và ProductDetail vào DB
        productService.save(product);

        model.addAttribute("message", "Product created successfully!");
        return "redirect:/manager/product/list";
    }


    @GetMapping("/edit/{proId}")
    public String editProduct(@PathVariable Integer proId, ModelMap model) {
    Product product = productService.findById(proId); // Lấy thông tin sản phẩm
    List<Category> categories = categoryService.findAll(); // Lấy danh sách category
    List<Promotion> promotions = promotionService.findAll(); // Lấy danh sách promotion

    model.addAttribute("product", product);
    model.addAttribute("categories", categories);
    model.addAttribute("promotions", promotions);
    model.addAttribute("isEdit", true); // Đặt flag isEdit để nhận biết
    return "manager/products/apps-ecommerce-product-create"; // Tên view

    }

    @PostMapping("/edit/save")
    public String updateProduct(
            @RequestParam("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam("promotionId") Integer promotionId,
            @RequestParam("categoryId") Integer categoryId,
            ModelMap model) {

        Product product = productService.findById(id);
        if (product == null) {
            model.addAttribute("errorMessage", "Product not found!");
            return "redirect:/manager/product/list";
        }

        product.setName(name);
        product.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                product.setImageLink("/uploads/" + fileName);
                imageFile.transferTo(new File("uploads/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Category category = categoryService.findById(categoryId);
        Promotion promotion = promotionService.findById(promotionId);

        if (category == null || promotion == null) {
            model.addAttribute("errorMessage", "Invalid category or promotion ID!");
            return "redirect:/manager/product/list";
        }

        product.setCategory(category);
        product.setPromotion(promotion);

        productService.save(product);
        model.addAttribute("message", "Product updated successfully!");
        return "redirect:/manager/product/list";
    }


    @PostMapping("/delete/{proId}")
    public String deleteProduct(@PathVariable("proId") Integer proId, ModelMap model) {
    Product product = productService.findById(proId);

    if (product == null) {
        model.addAttribute("errorMessage", "Product not found!");
        return "redirect:/manager/product/list";
    }
    productService.deleteById(proId);

    model.addAttribute("message", "Delete success");
    return "redirect:/manager/product/list";
    }
        @GetMapping("/top-selling")
        public String getTopSellingProducts(Model model) {
            List<BestSellingProductDTO> topProducts = productService.getTop6BestSellingProducts();
            model.addAttribute("topProducts", topProducts);
            return "top-selling-products";
        }
    }
