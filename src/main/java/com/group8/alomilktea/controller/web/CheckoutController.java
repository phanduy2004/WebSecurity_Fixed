package com.group8.alomilktea.controller.web;

import com.group8.alomilktea.entity.Cart;
import com.group8.alomilktea.entity.Promotion;
import com.group8.alomilktea.entity.ShipmentCompany;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.model.CartModel;
import com.group8.alomilktea.model.ProductDetailDTO;
import com.group8.alomilktea.service.ICartService;
import com.group8.alomilktea.service.IPromotionService;
import com.group8.alomilktea.service.IShipmentCompany;
import com.group8.alomilktea.service.IUserService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    String PaymentMethod;
    String Note;
    @Autowired(required = true)
    IUserService userService;
    @Autowired(required = true)
    ICartService cartService;
    @Autowired
    IPromotionService promoService;

    @Autowired(required = true)
    IShipmentCompany shipmentCompany;

    @GetMapping("")
    public String ThongtinKh(ModelMap model) {
        User user = userService.getUserLogged();

        String add = user.getAddress();
        if (add != null && !add.trim().isEmpty()) {
            String[] parts = add.split("\\s*,\\s*");
            if (parts.length >= 4) { // Ensure the array has enough parts
                model.addAttribute("province", parts[3].trim());
                model.addAttribute("city", parts[2].trim());
                model.addAttribute("commune", parts[1].trim());
                model.addAttribute("address", parts[0].trim());
            } else if (parts.length == 3) {
                model.addAttribute("commune", parts[2].trim());
                model.addAttribute("city", parts[1].trim());
                model.addAttribute("address", parts[0].trim());
            } else if (parts.length == 2) {
                model.addAttribute("city", parts[1].trim());
                model.addAttribute("address", parts[0].trim());
            } else if (parts.length == 1) {
                model.addAttribute("address", parts[0].trim());
            }
        } else {
            // Handle case where add is null or empty
            model.addAttribute("address", "No address provided");
        }


        model.addAttribute("user", user);
        List<Cart> cartItems = cartService.findByUserId(user.getUserId());

        // Tính tổng tiền hàng
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        // Kiểm tra và thay thế NaN nếu cần
        if (Double.isNaN(totalAmount)) {
            totalAmount = 0.0;
            System.out.println("Bị lôi totalAmount");
        }

        // Lấy phí vận chuyển (thay đổi phần này cho phù hợp với logic của bạn)
        double shipCost = 0; // Giá trị mặc định
        List<ShipmentCompany> shippingMethods = shipmentCompany.findAll();


        shipCost = shippingMethods.get(0).getPrice(); //Lấy phí vận chuyển của phương thức đầu tiên
        int shipid   = shippingMethods.get(0).getShipCid();
        System.out.println("Bị lôi shipcost" + shipCost);


        // Giảm giá (ban đầu là 0)
        double discount = 0;

        // Kiểm tra và thay thế NaN nếu cần


        // Tính tổng cộng
        double grandTotal = totalAmount + shipCost - discount;
        System.out.println("Bị lôi shipcost" + grandTotal);
        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("promotions", promoService.findAll());
        model.addAttribute("shipcost", shipCost);
        model.addAttribute("shipid", shipid);
        model.addAttribute("discount", discount);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("shippingMethods", shippingMethods); //Thêm shippingMethods vào model
        return "web/billy/checkout";
    }


}
