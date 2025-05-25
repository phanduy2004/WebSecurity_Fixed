package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = {"web/product"})
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IProductService productService;

    @Autowired(required = true)
    ICartService cartService;

    @Autowired(required = true)
    IUserService userService;

    @Autowired(required = true)
    IWishlistService wishlistService;

    @Autowired
    ISessionService sessionService;

    @GetMapping()
    public String trangchu(Model model) {
        String currentUserName = "1";
        List<ProductDetailDTO> list = productService.findProductInfoBySize();
        model.addAttribute("products", list);
        model.addAttribute("userId", currentUserName);
        return "web/billy/index";
    }

    @GetMapping("detail/{id}")
    public String product(@PathVariable("id") Integer id, Model model) {
        Product product1 = productService.findById(id);

        if (product1 == null) {
            model.addAttribute("errorMessage", "Sản phẩm với ID " + id + " không tồn tại.");
            return "error/custom_error";
        }

        Integer categoryId = product1.getCategory().getCateId();
        Page<ProductDetailDTO> relatedProductsPage = productService.findProductInfoByCatIDPaged(categoryId, 1, 5);
        List<ProductDetailDTO> relatedProducts = relatedProductsPage.getContent();
        model.addAttribute("relatedProducts", relatedProducts);

        User user = userService.getUserLogged();
        if (user != null) {
            SessionKey sessionKey = new SessionKey(user.getUserId(), id);
            Session session = new Session();
            session.setId(sessionKey);
            session.setProduct(product1);
            session.setUser(user);
            session.setDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
            sessionService.save(session);
        }
        List<ProductDetailDTO> product = productService.findProductInfoByID(id);
        model.addAttribute("prd", product);
        return "web/billy/product-details";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String invalidValue = (ex.getValue() != null) ? ex.getValue().toString() : "null";

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
        mav.addObject("errorDetails", "Giá trị '" + invalidValue + "' không thể được sử dụng làm ID sản phẩm.");
        mav.setViewName("error/custom_error");
        return mav;
    }

    @RequestMapping("/getPrice")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPrice(@RequestParam Integer productId, @RequestParam String size) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductAttribute attribute = ProductAttribute.getEnum(size);
            ProductDetail product = productService.findPriceByProductIdAndSize(productId, attribute);
            if (product != null) {
                response.put("success", true);
                response.put("price", product.getPrice());
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm với kích thước này.");
            }
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Kích thước không hợp lệ: " + size);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy giá sản phẩm.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/addToCart")
    public ResponseEntity<String> addToCart(
            @RequestParam("productId") Integer proId,
            @RequestParam("qty") Integer qty,
            @RequestParam("size") String size,
            @RequestParam("price") Double price,
            HttpServletRequest request) {

        try {
            User userLogged = userService.getUserLogged();
            if (userLogged == null) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "/auth/login")
                        .body("Vui lòng đăng nhập để tiếp tục.");
            }

            ProductAttribute attribute = ProductAttribute.getEnum(size);
            ProductDetail product1 = productService.findPriceByProductIdAndSize(proId, attribute);

            Optional<Product> optProduct = productService.findById0p(proId);
            if (optProduct.isEmpty()) {
                return new ResponseEntity<>("Sản phẩm không tồn tại", HttpStatus.NOT_FOUND);
            }
            Product product = optProduct.get();

            List<Cart> cartList = cartService.findByUserId(userLogged.getUserId());
            Optional<Cart> existingCartItem = cartList.stream()
                    .filter(cart -> cart.getProduct().getProId().equals(proId) && cart.getId().getSize().equals(size))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                Cart cart = existingCartItem.get();
                int updatedQuantity = cart.getQuantity() + qty;
                cart.setQuantity(updatedQuantity);
                cartService.save(cart);
            } else {
                CartKey cartKey = new CartKey(userLogged.getUserId(), proId, size);
                Cart newCart = new Cart();
                newCart.setId(cartKey);
                newCart.setProduct(product);
                newCart.setUser(userLogged);
                newCart.getId().setSize(size);
                newCart.setQuantity(qty);
                newCart.setPrice(product1.getPrice());
                cartService.save(newCart);
            }
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/user-cart")
                    .body("Thêm vào giỏ hàng thành công");

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Kích thước sản phẩm không hợp lệ: " + size, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thêm sản phẩm vào giỏ hàng.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/addToWishlist")
    public ResponseEntity<String> addToWishlist(
            @RequestParam("productId") Integer proId,
            @RequestParam("qty") Integer qty,
            @RequestParam("size") String size,
            @RequestParam("price") Double price,
            HttpServletRequest request) {

        try {
            User userLogged = userService.getUserLogged();
            if (userLogged == null) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "/auth/login")
                        .body("Vui lòng đăng nhập để tiếp tục.");
            }

            ProductAttribute attribute = ProductAttribute.getEnum(size);
            ProductDetail product1 = productService.findPriceByProductIdAndSize(proId, attribute);

            Optional<Product> optProduct = productService.findById0p(proId);
            if (optProduct.isEmpty()) {
                return new ResponseEntity<>("Sản phẩm không tồn tại", HttpStatus.NOT_FOUND);
            }
            Product product = optProduct.get();

            List<Wishlist> wishList = wishlistService.findByUserId(userLogged.getUserId());
            Optional<Wishlist> existingWishlistItem = wishList.stream()
                    .filter(item -> item.getProduct().getProId().equals(proId) && item.getId().getSize().equals(size))
                    .findFirst();

            String successMessage = "Thêm vào danh sách yêu thích thành công";
            if (existingWishlistItem.isPresent()) {
                Wishlist wishlist = existingWishlistItem.get();
                int updatedQuantity = wishlist.getQuantity() + qty;
                wishlist.setQuantity(updatedQuantity);
                wishlistService.save(wishlist);
            } else {
                CartKey cartKey = new CartKey(userLogged.getUserId(), proId, size);
                Wishlist newWishlist = new Wishlist();
                newWishlist.setId(cartKey);
                newWishlist.setProduct(product);
                newWishlist.setUser(userLogged);
                newWishlist.getId().setSize(size);
                newWishlist.setQuantity(qty);
                newWishlist.setPrice(product1.getPrice());
                wishlistService.save(newWishlist);
            }
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/user-wishList")
                    .body(successMessage);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Kích thước sản phẩm không hợp lệ: " + size, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thêm sản phẩm vào danh sách yêu thích.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}