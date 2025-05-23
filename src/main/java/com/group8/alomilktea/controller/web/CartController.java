package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.model.CartModel;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.repository.CartRepository;
import com.group8.alomilktea.service.ICartService;
import com.group8.alomilktea.service.IProductService;
import com.group8.alomilktea.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/user-cart")
@Controller
public class CartController {
    @Autowired(required=true)
    IUserService userService;
    @Autowired(required=true)
    ICartService cartService;
    @Autowired(required=true)
    IProductService productService;
    @Autowired(required = true)
    IProductService proService;
    @Autowired
    private CartRepository cartRepository;


    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItem(
            @RequestParam("user_id") Integer userId,
            @RequestParam("pro_id") Integer productId,
            @RequestParam("size") String size) {

        try {
            CartKey cartKey = new CartKey(userId, productId, size);
            if (cartService.existsById(cartKey)) {
                cartService.deleteById(cartKey);
                return ResponseEntity.ok(Map.of("success", true, "message", "Xóa sản phẩm thành công"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy sản phẩm trong giỏ hàng"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Lỗi server"));
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateItemQuantity(
            @RequestParam("user_id") Integer userId,
            @RequestParam("pro_id") Integer productId,
            @RequestParam("size") String size,
            @RequestParam("quantity") Integer quantity) {

        try {
            CartKey cartKey = new CartKey(userId, productId, size);
            Optional<Cart> cartItemOptional = cartRepository.findById(cartKey);

            if (cartItemOptional.isPresent()) {
                Cart cartItem = cartItemOptional.get();
                cartItem.setQuantity(quantity);  // Cập nhật số lượng sản phẩm
                cartRepository.save(cartItem);  // Lưu vào DB

                // Trả về thông báo thành công
                return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật số lượng thành công"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy sản phẩm trong giỏ hàng"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Lỗi server"));
        }
    }


}




