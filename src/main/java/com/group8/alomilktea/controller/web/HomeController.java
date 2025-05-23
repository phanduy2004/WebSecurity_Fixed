package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.model.CategoryModel;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.model.UserModel;
import com.group8.alomilktea.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping(value = {"","/","home","trang-chu"})
@Controller
public class HomeController{
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    IProductService productService;
    @Autowired
    IUserService userService;
    @Autowired
    ICartService cartService;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IWishlistService wishlistService;
    @Autowired
    ISessionService sessionService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            User userLogged = userService.getUserLogged(); // Lấy thông tin người dùng đã đăng nhập
            List<Cart> cartItems = Collections.emptyList(); // Khởi tạo giỏ hàng rỗng mặc định
            double totalAmount = 0.0; // Giá trị mặc định của tổng giá trị giỏ hàng

            if (userLogged != null) { // Nếu người dùng đã đăng nhập
                cartItems = cartService.findByUserId(userLogged.getUserId()); // Lấy giỏ hàng
                totalAmount = cartItems.stream()
                        .mapToDouble(cart -> cart.getQuantity() * cart.getPrice())
                        .sum(); // Tính tổng giá trị giỏ hàng
            }

            model.addAttribute("cartItems", cartItems); // Đưa giỏ hàng vào model
            model.addAttribute("totalAmount", totalAmount); // Đưa tổng giá trị giỏ hàng vào model

        } catch (Exception e) {
            logger.error("Lỗi khi tải thông tin giỏ hàng: ", e);
            model.addAttribute("cartItems", Collections.emptyList()); // Nếu xảy ra lỗi, giỏ hàng rỗng
            model.addAttribute("totalAmount", 0.0); // Tổng giá trị mặc định là 0
        }
    }

    @GetMapping()
    public String trangchu(Model model){
        List<ProductDetailDTO> list = productService.findProductInfoBySize();
        List<Category> listcat = categoryService.findAll();
        model.addAttribute("products", list);
        model.addAttribute("categories", listcat);
        if (list.isEmpty()) {
            System.out.println("No products found");
        } else {
            System.out.println("Hello anhh em" + list);
        }
        return "web/billy/index";
    }

    @GetMapping("/user-cart")
    public String viewCart(Model model) {
        try {
            User userLogged = userService.getUserLogged(); // Lấy thông tin người dùng đã đăng nhập

            if (userLogged == null) {
                return "redirect:auth/login"; // Nếu người dùng chưa đăng nhập, chuyển hướng tới trang đăng nhập
            }

            // Lấy danh sách sản phẩm trong giỏ hàng của người dùng
            List<Cart> cartItems = cartService.findByUserId(userLogged.getUserId());

            // Tính tổng giá trị của giỏ hàng
            double totalAmount = cartItems.stream()
                    .mapToDouble(cart -> cart.getQuantity() * cart.getPrice()) // Giá * Số lượng
                    .sum();

            // Đưa danh sách sản phẩm và tổng giá trị vào model
            model.addAttribute("cartItems", cartItems); // Danh sách giỏ hàng
            model.addAttribute("totalAmount", totalAmount); // Tổng giá trị giỏ hàng

            return "web/billy/cart-page";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Không thể tải giỏ hàng. Vui lòng thử lại sau.");
            return "error"; // Trả về trang lỗi
        }
    }

    @GetMapping("/myaccount")
    public String myAccount(ModelMap model) {
        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Optional<User> optUser = userService.findByEmail(email);
            if (optUser.isPresent()) {
                User user = optUser.get();
                UserModel userModel = new UserModel();
                BeanUtils.copyProperties(user, userModel);
                userModel.setIsEdit(true);
                String name =userModel.getUsername();
                // Retrieve address and handle possible null or empty values
                String add = userModel.getAddress();
                if (add != null && !add.trim().isEmpty()) {
                    // Split the address into parts
                    String[] parts = add.split("\\s*,\\s*");

                    // Check if there are enough parts
                    if (parts.length >= 3) {
                        model.addAttribute("homeaddress", parts[0].trim());
                        model.addAttribute("town", parts[1].trim());
                        model.addAttribute("district", parts[2].trim());
                        model.addAttribute("city", parts[3].trim()); // Assuming parts[3] exists
                    } else {
                        // If the address format is not as expected, just use the full address
                        model.addAttribute("homeaddress", add.trim());
                    }
                } else {
                    // If the address is null or empty, set homeaddress to a default value or handle it
                    model.addAttribute("homeaddress", "No address provided");
                }
                // Lấy danh sách sản phẩm trong giỏ hàng của người dùng
                List<Session> sessions = sessionService.findAllSortedByDate();
                model.addAttribute("viewedItems", sessions);
                model.addAttribute("user", userModel);
                return "web/users/my-account";
            }
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/api/categories")
    @ResponseBody
    public List<CategoryModel> getCategories() {
        List<Category> categories = categoryService.findAll();
        System.out.println("Categories: " + categories);  // Log thông tin danh mục ra console
        List<CategoryModel> categoryDTOs = categories.stream()
                .map(category -> new CategoryModel(category.getCateId(), category.getName()))
                .collect(Collectors.toList());
        return categoryDTOs;
    }
    @GetMapping("/user-wishList")
    public String viewWishlist(Model model) {
        try {
            User userLogged = userService.getUserLogged(); // Lấy thông tin người dùng đã đăng nhập

            if (userLogged == null) {
                return "redirect:auth/login"; // Nếu người dùng chưa đăng nhập, chuyển hướng tới trang đăng nhập
            }

            // Lấy danh sách sản phẩm trong giỏ hàng của người dùng
            List<Wishlist> wishlistItems = wishlistService.findByUserId(userLogged.getUserId());

            // Tính tổng giá trị của giỏ hàng
            double totalAmount = wishlistItems.stream()
                    .mapToDouble(cart -> cart.getQuantity() * cart.getPrice()) // Giá * Số lượng
                    .sum();

            // Đưa danh sách sản phẩm và tổng giá trị vào model
            model.addAttribute("wishItems", wishlistItems); // Danh sách giỏ hàng
            model.addAttribute("total",totalAmount);
            return "web/billy/wishlist";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Không thể tải giỏ hàng. Vui lòng thử lại sau.");
            return "error"; // Trả về trang lỗi
        }
    }

    @GetMapping("/about")
    private String showAboutPage(){
        return "web/billy/about-us";
    }
}
