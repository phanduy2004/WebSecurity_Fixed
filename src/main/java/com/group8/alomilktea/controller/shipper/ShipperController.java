package com.group8.alomilktea.controller.shipper;


import com.group8.alomilktea.entity.Order;
import com.group8.alomilktea.entity.User;
import com.group8.alomilktea.model.UserModel;
import com.group8.alomilktea.service.IOrderService;
import com.group8.alomilktea.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ShipperController {

    @Autowired(required = true)
    IOrderService orderSer;

    @Autowired()
    IUserService userService;

    @RequestMapping("/shipper/dashboard")
    public String dashboard(ModelMap model){
        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Long Cid = userService.findShipCIdByUser(email);
            if (Cid != null) {
                long shipod = orderSer.countByStatusAndShip("Shipping",Cid);
                long deleod = orderSer.countByStatusAndShip("Delivered",Cid);
                long cancelod = orderSer.countByStatusAndShip("Cancelled",Cid);
                long returnod = orderSer.countByStatusAndShip("Return",Cid);
                long totalOrders = orderSer.countbyShipID(Cid);
                model.addAttribute("totalOrders", totalOrders);
                model.addAttribute("returnod", returnod);
                model.addAttribute("shipod", shipod);
                model.addAttribute("deliod", deleod);
                model.addAttribute("cancelod", cancelod);
                return "shipper/orders/thongke";
            }
        }
        return "redirect:/auth/login";

    }

    @RequestMapping("/shipper/listShip")
    public String listOrders(
            ModelMap model,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {

        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Long Cid = userService.findShipCIdByUser(email);
            if (Cid != null) {
                long shipod = orderSer.countByStatusAndShip("Shipping",Cid);
                Page<Order> page = orderSer.getOderByStatus(pageNo, "Shipping",Cid);
                model.addAttribute("orders", page.getContent());
                model.addAttribute("totalPage", page.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("shipod", shipod);
                return "shipper/orders/apps-ecommerce-orders";
            }
        }
        return "redirect:/auth/login";

    }
    @RequestMapping("/shipper/listDeliver")
    public String listOrders1(
            ModelMap model,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Long Cid = userService.findShipCIdByUser(email);
            if (Cid != null) {
                long deleod = orderSer.countByStatusAndShip("Delivered",Cid);
                Page<Order> page = orderSer.getOderByStatus(pageNo, "Delivered",Cid);
                model.addAttribute("orders", page.getContent());
                model.addAttribute("totalPage", page.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("deliod", deleod);
                return "shipper/orders/apps-ecommerce-orders";
            }
        }
        return "redirect:/auth/login";
    }
    @RequestMapping("/shipper/listCancel")
    public String listOrders2(
            ModelMap model,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Long Cid = userService.findShipCIdByUser(email);
            if (Cid != null) {
                long cancelod = orderSer.countByStatusAndShip("Cancelled",Cid);
                Page<Order> page = orderSer.getOderByStatus(pageNo, "Cancelled",Cid);

                model.addAttribute("orders", page.getContent());
                model.addAttribute("totalPage", page.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("cancelod", cancelod);
                return "shipper/orders/apps-ecommerce-orders";
            }
        }
        return "redirect:/auth/login";

    }
    @RequestMapping("/shipper/listReturn")
    public String listOrders3(
            ModelMap model,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        User userLogged = userService.getUserLogged(); // Get logged-in user details
        if (userLogged != null) {
            String email = userLogged.getEmail();
            Long Cid = userService.findShipCIdByUser(email);
            if (Cid != null) {
                long returnod = orderSer.countByStatusAndShip("Return",Cid);

                Page<Order> page = orderSer.getOderByStatus(pageNo, "Return",Cid);
                model.addAttribute("orders", page.getContent());
                model.addAttribute("totalPage", page.getTotalPages());
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("returnod", returnod);
                return "shipper/orders/apps-ecommerce-orders";
            }
        }
        return "redirect:/auth/login";



    }


    @PostMapping("shipper/updateState/{orderId}")
    public String updateOrderState(@PathVariable("orderId") Integer orderId, ModelMap modelMap) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderSer.findById(orderId);

        if (order != null) {
            String currentStatus = order.getStatus();

            // Kiểm tra và cập nhật trạng thái đơn hàng tùy theo yêu cầu
            if ("Shipping".equals(currentStatus)) {
                    order.setStatus("Delivered");
            } else {
                // Nếu trạng thái không hợp lệ, thông báo lỗi
                modelMap.addAttribute("error", "Trạng thái không hợp lệ!");
            }
            // Lưu thay đổi vào cơ sở dữ liệu
            orderSer.save(order);
        }
        // Trả về thông tin đơn hàng và thông báo lỗi (nếu có)
        modelMap.addAttribute("order", order);
        return "redirect:/shipper/listShip"; // Quay lại danh sách đơn hàng
    }

    @PostMapping("shipper/reject/{orderId}")
    public String updateOrderState2(@PathVariable("orderId") Integer orderId, ModelMap modelMap) {
        // Lấy đơn hàng từ cơ sở dữ liệu
        Order order = orderSer.findById(orderId);

        if (order != null) {
            String currentStatus = order.getStatus();

            // Kiểm tra và cập nhật trạng thái đơn hàng tùy theo yêu cầu
            if ("Shipping".equals(currentStatus)) {
                order.setStatus("Cancelled");
            } else if ("Delivered".equals(currentStatus))
            {
                order.setStatus("Return");
            }
            else
            {
                // Nếu trạng thái không hợp lệ, thông báo lỗi
                modelMap.addAttribute("error", "Trạng thái không hợp lệ!");
            }
            // Lưu thay đổi vào cơ sở dữ liệu
            orderSer.save(order);
        }
        // Trả về thông tin đơn hàng và thông báo lỗi (nếu có)
        modelMap.addAttribute("order", order);
        return "redirect:/shipper/listShip"; // Quay lại danh sách đơn hàng
    }

}
