package com.group8.alomilktea.controller.manager;

import com.group8.alomilktea.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class ManagerDashboardController {
    @Autowired(required = true)
    IOrderService orderService;
   /* @RequestMapping("/manager")
    public String dashboard(ModelMap model){
        Locale localeVietnam = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVietnam);

        model.addAttribute("reMonth",currencyFormatter.format(orderService.reOnCurrentMonth()));
        model.addAttribute("reYear",currencyFormatter.format(orderService.reOnCurrentYear()));
        model.addAttribute("reQuarter",currencyFormatter.format(orderService.reOnCurrentQuarter()));
        model.addAttribute("rateCom",orderService.rateCom());

        model.addAttribute("totalMontly", orderService.getMonthlyTotal());
        model.addAttribute("totalQuarter", orderService.getQuarterTotal());

        return "manager/dashboard";
    }*/
   @RequestMapping("/manager")
   public String dashboard(ModelMap model){
       // Lấy doanh thu cả năm
       int revenueYear = orderService.reOnCurrentYear();

       // Định dạng tiền tệ Việt Nam
       Locale localeVietnam = new Locale("vi", "VN");
       NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVietnam);
       String formattedRevenue = currencyFormatter.format(revenueYear);

       // Truyền doanh thu xuống view
       model.addAttribute("revenueYear", formattedRevenue);

       // Lấy tổng số đơn hàng
       long totalOrders = orderService.countOrder();

       model.addAttribute("totalOrders", totalOrders);
       // Lấy tổng số đơn hàng đã giao
       long deliveredOrders = orderService.countShippingOrders();  // Lấy số đơn đã giao
       System.out.println("Delivered Orders: " + deliveredOrders);  // Kiểm tra giá trị trong console

       // Truyền dữ liệu vào ModelMap
       model.addAttribute("deliveredOrders", deliveredOrders);

       long cancelledOrders = orderService.countCancelOrders();  // Lấy số đơn đã hủy

       model.addAttribute("cancelledOrders", cancelledOrders);


       return "manager/index";
   }
}
