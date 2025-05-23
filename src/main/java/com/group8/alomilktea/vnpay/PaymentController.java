package com.group8.alomilktea.vnpay;


import com.group8.alomilktea.config.response.ResponseObject;
import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService; // Đã thêm final
    private final IUserService userService;
    private final ICartService cartService;
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IShipmentCompany shipmentCompany;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<String> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String fullAddress = paymentService.fullAddress;
        String shippingId = paymentService.shipingid;
        if ("00".equals(status)) {
            // Lấy thông tin người dùng
            User user = userService.getUserLogged();

            // Lấy danh sách sản phẩm trong giỏ hàng
            List<Cart> carts = cartService.findByUserId(user.getUserId());
            if (carts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("<html><body><h3>Giỏ hàng trống!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
            }
            int shipingid = Integer.parseInt(shippingId);
            ShipmentCompany shipment = shipmentCompany.findById(shipingid);
            System.out.println(shipment+ "helloooship");
            // Tính tổng giá phải thanh toán
            Double totalAmount = (double) paymentService.amount; // Lấy giá trị từ PaymentService

            // Tạo đơn đặt hàng
            Order order = new Order();
            order.setUser(user);
            order.setCurrency("VND");
            order.setTotal(totalAmount);
            order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            order.setPaymentMethod("VNPay");
            order.setStatus("Pending");
            order.setDeliAddress(fullAddress);
            order.setShipmentCompany(shipment);
            orderService.save(order);

            // Tạo chi tiết đơn đặt hàng
            for (Cart cart : carts) {
                if (cart.getId() == null || cart.getProduct() == null || cart.getId().getSize() == null) {
                    System.out.println("Cart item missing data, skipping: " + cart);
                    continue; // Bỏ qua nếu thiếu dữ liệu
                }

                OrderDetailKey orderDetailKey = new OrderDetailKey(order.getOrderId(), cart.getProduct().getProId(), cart.getId().getSize());
                System.out.println("OrderDetailKey to save: " + orderDetailKey);

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setId(orderDetailKey);
                orderDetail.setProduct(cart.getProduct());
                orderDetail.setQuantity(cart.getQuantity());
                System.out.println("OrderDetail to save: " + orderDetail);

                orderDetailService.save(orderDetail);
            }

            // Xóa giỏ hàng
            cartService.clearCart(user.getUserId());

            // Trả về HTML thông báo và chuyển hướng
            return ResponseEntity.ok("<html><body>" +
                    "<h3>Thanh toán thành công và đơn hàng đã được tạo!</h3>" +
                    "<p>Bạn sẽ được chuyển hướng về trang chủ sau 5 giây...</p>" +
                    "<meta http-equiv='refresh' content='5;url=/'>" +
                    "</body></html>");
        } else {
            // Thanh toán thất bại
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Thanh toán thất bại!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }
    }
    @GetMapping("/cash-on-delivery")
    public ResponseEntity<?> processCashOnDelivery(
            @RequestParam String grandTotalAmount,
            @RequestParam String province,
            @RequestParam String city,
            @RequestParam String commune,
            @RequestParam String address,
            @RequestParam String shippingMethodId) {

        // Log payment data for debugging
        String fullAddress = address + ", " + commune + ", " + city + ", " + province;

        // Simulate the processing of the COD order (e.g., storing order data)
        int shipingid = Integer.parseInt(shippingMethodId);

        ShipmentCompany shipment = shipmentCompany.findById(shipingid);
        System.out.println("lo cc" + shipment);
        User user = userService.getUserLogged();

        // Lấy danh sách sản phẩm trong giỏ hàng
        List<Cart> carts = cartService.findByUserId(user.getUserId());
        if (carts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Giỏ hàng trống!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }

        // Tính tổng giá phải thanh toán
        Double totalAmount = Double.valueOf(grandTotalAmount); // Lấy giá trị từ PaymentService

        // Tạo đơn đặt hàng
        Order order = new Order();
        order.setUser(user);
        order.setCurrency("VND");
        order.setTotal(totalAmount);
        order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        order.setPaymentMethod("CashOnDelivery");
        order.setStatus("Pending");
        order.setDeliAddress(fullAddress);
        order.setShipmentCompany(shipment);
        orderService.save(order);

        // Tạo chi tiết đơn đặt hàng
        for (Cart cart : carts) {
            if (cart.getId() == null || cart.getProduct() == null || cart.getId().getSize() == null) {
                System.out.println("Cart item missing data, skipping: " + cart);
                continue; // Bỏ qua nếu thiếu dữ liệu
            }

            OrderDetailKey orderDetailKey = new OrderDetailKey(order.getOrderId(), cart.getProduct().getProId(), cart.getId().getSize());
            System.out.println("OrderDetailKey to save: " + orderDetailKey);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(orderDetailKey);
            orderDetail.setProduct(cart.getProduct());
            orderDetail.setQuantity(cart.getQuantity());
            System.out.println("OrderDetail to save: " + orderDetail);

            orderDetailService.save(orderDetail);
        }

        // Xóa giỏ hàng
        cartService.clearCart(user.getUserId());
        return ResponseEntity.ok("<html><body>" +
                "<h3>Thanh toán thành công và đơn hàng đã được tạo!</h3>" +
                "<p>Bạn sẽ được chuyển hướng về trang chủ sau 5 giây...</p>" +
                "<meta http-equiv='refresh' content='5;url=/'>" +
                "</body></html>");
    }
}



