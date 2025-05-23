package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.service.ICartService;
import com.group8.alomilktea.service.IProductService;
import com.group8.alomilktea.service.IUserService;
import com.group8.alomilktea.service.IWishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = {"/user-wishList"})  // Cố định id = 1
@Controller
public class WishlistController {
    @Autowired
    private IProductService productService;

    @Autowired(required=true)
    ICartService cartService;

    @Autowired(required=true)
    IUserService userService;
    @Autowired(required=true)
    IWishlistService wishlistService;

    @GetMapping("/addToCart")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam("user_id") int userId,
            @RequestParam("pro_id") int productId,
            @RequestParam("size") String size,
            @RequestParam("qty") int quantity,
            @RequestParam("price") double price) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 1️⃣ Kiểm tra xem người dùng đã đăng nhập chưa
            User userLogged = userService.getUserLogged();
            if (userLogged == null) {
                response.put("success", false);
                response.put("message", "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 2️⃣ Tìm sản phẩm từ cơ sở dữ liệu
            Optional<Product> optProduct = productService.findById0p(productId);
            if (optProduct.isEmpty()) {
                response.put("success", false);
                response.put("message", "Sản phẩm không tồn tại.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Product product = optProduct.get();

            // 3️⃣ Lấy danh sách giỏ hàng của người dùng
            List<Cart> cartList = cartService.findByUserId(userLogged.getUserId());

            // 4️⃣ Tìm sản phẩm trong giỏ hàng (kiểm tra cùng `proId` và `size`)
            Optional<Cart> existingCartItem = cartList.stream()
                    .filter(cart -> cart.getProduct().getProId().equals(productId)
                            && cart.getId().getSize().equals(size))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                // 4.1️⃣ Nếu sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng
                Cart cart = existingCartItem.get();
                int updatedQuantity = cart.getQuantity() + quantity;

                // Kiểm tra tồn kho nếu cần (không có logic kiểm tra kho ở đây)
                cart.setQuantity(updatedQuantity);
                cartService.save(cart);

            } else {
                // 4.2️⃣ Nếu sản phẩm chưa tồn tại, thêm sản phẩm mới vào giỏ hàng
                CartKey cartKey = new CartKey(userLogged.getUserId(), productId, size);
                Cart newCart = new Cart();
                newCart.setId(cartKey);
                newCart.setProduct(product);
                newCart.setUser(userLogged);
                newCart.setQuantity(quantity);
                newCart.setPrice(price);
                cartService.save(newCart);
            }

            // 5️⃣ Xóa sản phẩm khỏi wishlist
            CartKey wishlistKey = new CartKey(userId, productId, size);

                wishlistService.deleteById(wishlistKey);


            // 6️⃣ Trả về phản hồi thành công
            response.put("success", true);
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng và xóa khỏi wishlist.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
