package com.group8.alomilktea.controller.admin;

import com.group8.alomilktea.entity.Order;
import com.group8.alomilktea.service.IOrderService;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    @Autowired(required = true)
    IOrderService orderSer;

    @Autowired()
    IUserService userService;
    @RequestMapping("/list")
    public String listOrders(
            ModelMap model,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        long newod = orderSer.countByStatus("New");
        long pendingod = orderSer.countByStatus("Pending");
        long shipod = orderSer.countByStatus("Shipping");
        long deleod = orderSer.countByStatus("Delivered");
        long cancelod = orderSer.countByStatus("Cancelled");

        System.out.println(pendingod + 5);

        Page<Order> page = orderSer.getAll(pageNo);
        long totalOrders = orderSer.count();
        model.addAttribute("orders", page.getContent());
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("newod", newod);
        model.addAttribute("pendingod", pendingod);
        model.addAttribute("shipod", shipod);
        model.addAttribute("deliod", deleod);
        model.addAttribute("cancelod", cancelod);
        return "admin/orders/apps-ecommerce-orders";
    }


    @PostMapping("updateState/{orderId}")
    public String updateOrderState(@PathVariable("orderId") Integer orderId, String newState, ModelMap modelMap) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderSer.findById(orderId);

        // Xử lý lỗi nếu trạng thái không phải là "Pending"
        String error = "";
        if (order.getStatus().equals("Pending") && newState.equals("Shipping")) {
            // Chuyển trạng thái của đơn hàng từ "Pending" sang "Shipping"
            order.setStatus("Shipping");
        } else if (!order.getStatus().equals("Pending")) {
            // Nếu trạng thái không phải "Pending", không thể thay đổi
            error = "Bạn chỉ có thể duyệt đơn hàng đang chờ xử lý";
        }

        // Lưu trạng thái mới của đơn hàng
        orderSer.save(order);

        // Trả về thông tin đơn hàng và thông báo lỗi (nếu có)
        modelMap.addAttribute("order", order);
        modelMap.addAttribute("error", error);

        return "redirect:/admin/orders/list"; // Quay lại danh sách đơn hàng
    }



    @GetMapping("delete/{orderId}")
    public ModelAndView deleteOder(ModelMap model, @PathVariable("orderId") Integer orderId) {
        try {
            orderSer.deleteById(orderId);
            model.addAttribute("message", "Order is deleted!!!!");
        } catch (Exception e) {
            model.addAttribute("message", "Cannot delete!!!!");
        }
        return new ModelAndView("forward:/admin/orders", model);
    }
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Integer userId) {
        return orderSer.findOder(userId);
    }
}
