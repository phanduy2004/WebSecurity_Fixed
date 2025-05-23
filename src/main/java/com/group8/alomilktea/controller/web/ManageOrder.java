package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.Order;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/myoders")
public class ManageOrder {
    @Autowired
    private IProductService productService;

    @Autowired(required=true)
    ICartService cartService;

    @Autowired(required=true)
    IUserService userService;
    @Autowired(required=true)
    IWishlistService wishlistService;
    @Autowired(required=true)
    IOrderService orderService;

    @GetMapping()
    public String viewMyOrder(Model model) {
        // Lấy danh sách đơn hàng của người dùng (ví dụ, lấy theo userId)
        User user = userService.getUserLogged();
        if(user==null){
            return "redirect:/auth/login";
        }
        List<Order> orders = orderService.findOder(user.getUserId());

        // Truyền thông tin đơn hàng vào model để hiển thị trong view
        model.addAttribute("orders", orders);

        // Trả về tên view sẽ hiển thị (web/users/ManageOrders)
        return "web/users/ManageOrders";
    }
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.updateStatus(orderId, "Canceled");
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    @PutMapping("/return/{orderId}")
    public ResponseEntity<?> returnOrder(@PathVariable Long orderId) {
        try {
            orderService.updateStatus(orderId, "Return");
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

}
