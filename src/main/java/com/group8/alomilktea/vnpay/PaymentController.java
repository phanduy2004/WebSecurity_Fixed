package com.group8.alomilktea.vnpay;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.config.response.ResponseObject;
import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID; // For generating unique transaction references

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final IUserService userService;
    private final ICartService cartService;
    private final IOrderService orderService;
    private final IOrderDetailService orderDetailService;
    private final IShipmentCompany shipmentCompanyService;
    private final IProductService productService;
    private final IProductDetailService productDetailService;
    private final IPromotionService promotionService;


    private static final String VNPAY_SESSION_PREFIX = "VNPAY_TXN_";

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request,
                                                        @RequestParam String shippingMethodId,
                                                        @RequestParam String province,
                                                        @RequestParam String city,
                                                        @RequestParam String commune,
                                                        @RequestParam String address,
                                                        @RequestParam(required = false) String promotionCode) {
        User user = userService.getUserLogged();
        if (user == null) {
            return new ResponseObject<>(HttpStatus.UNAUTHORIZED, "User not logged in", null);
        }
        String fullAddress = address + ", " + commune + ", " + city + ", " + province;
        Integer shipIdInt = Integer.parseInt(shippingMethodId);
        Double serverCalculatedTotal = orderService.calculateGrandTotalFromServer(user, shipIdInt, promotionCode);
        if (serverCalculatedTotal <= 0) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Invalid order amount or empty cart.", null);
        }
        String vnpTxnRef = UUID.randomUUID().toString().replace("-", ""); // Tạo mã giao dịch duy nhất
        String orderInfo = "Thanh toan don hang " + vnpTxnRef + " cho " + user.getEmail();
        try {
            orderInfo = URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {}
        HttpSession session = request.getSession();
        session.setAttribute(VNPAY_SESSION_PREFIX + vnpTxnRef + "_TOTAL", serverCalculatedTotal);
        session.setAttribute(VNPAY_SESSION_PREFIX + vnpTxnRef + "_ADDRESS", fullAddress);
        session.setAttribute(VNPAY_SESSION_PREFIX + vnpTxnRef + "_SHIPPING_ID", shipIdInt);
        session.setAttribute(VNPAY_SESSION_PREFIX + vnpTxnRef + "_PROMO", promotionCode);
        session.setAttribute(VNPAY_SESSION_PREFIX + vnpTxnRef + "_USER_ID", user.getUserId());
        PaymentDTO.VNPayResponse vnPayResponse = paymentService.createVnPayPayment(request, serverCalculatedTotal, vnpTxnRef, orderInfo);
        return new ResponseObject<>(HttpStatus.OK, "Success", vnPayResponse);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<String> payCallbackHandler(HttpServletRequest request) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_AmountStr = request.getParameter("vnp_Amount"); // Số tiền VNPAY trả về (đơn vị nhỏ nhất)

        HttpSession session = request.getSession();
        Double expectedTotal = (Double) session.getAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_TOTAL");
        String fullAddress = (String) session.getAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_ADDRESS");
        Integer shippingId = (Integer) session.getAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_SHIPPING_ID");
        // String promotionCode = (String) session.getAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_PROMO");
        Integer userId = (Integer) session.getAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_USER_ID");

        // Xóa session attributes sau khi lấy
        session.removeAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_TOTAL");
        session.removeAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_ADDRESS");
        session.removeAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_SHIPPING_ID");
        session.removeAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_PROMO");
        session.removeAttribute(VNPAY_SESSION_PREFIX + vnp_TxnRef + "_USER_ID");

        if (expectedTotal == null || fullAddress == null || shippingId == null || userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Giao dịch không hợp lệ hoặc phiên hết hạn!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Người dùng không tồn tại!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }
        if ("00".equals(vnp_ResponseCode)) {
            double vnpAmountProcessed = Double.parseDouble(vnp_AmountStr) / 100.0;
            // So sánh số tiền VNPAY xử lý với số tiền server đã tính toán và lưu trong session
            if (Math.abs(vnpAmountProcessed - expectedTotal) > 0.01) { // Cho phép sai số nhỏ do làm tròn
                // Số tiền không khớp, giao dịch đáng ngờ
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("<html><body><h3>Giao dịch không hợp lệ: sai lệch số tiền!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
            }

            List<Cart> carts = cartService.findByUserId(user.getUserId());
            if (carts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("<html><body><h3>Giỏ hàng trống! Không thể tạo đơn hàng.</h3><a href='/'>Quay lại trang chủ</a></body></html>");
            }
            ShipmentCompany shipment = shipmentCompanyService.findById(shippingId);
            if (shipment == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("<html><body><h3>Phương thức vận chuyển không hợp lệ!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
            }

            Order order = new Order();
            order.setUser(user);
            order.setCurrency("VND");
            order.setTotal(expectedTotal); // Sử dụng tổng tiền đã xác minh từ server
            order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            order.setPaymentMethod("VNPay");
            order.setStatus("Pending"); // Hoặc "Paid" nếu VNPAY xác nhận đã thanh toán
            order.setDeliAddress(fullAddress);
            order.setShipmentCompany(shipment);
            Order savedOrder = orderService.save(order);

            for (Cart cart : carts) {
                Product dbProduct = productService.findById(cart.getProduct().getProId());
                if (dbProduct == null || cart.getId() == null || cart.getId().getSize() == null) {
                    System.err.println("Bỏ qua cart item không hợp lệ khi tạo order detail: " + cart);
                    continue;
                }
                ProductDetail productDetail = productDetailService.findPriceByProductIdAndSize(
                        dbProduct.getProId(),
                        ProductAttribute.getEnum(cart.getId().getSize())
                );
                if (productDetail == null || productDetail.getPrice() == null) {
                    System.err.println("Không tìm thấy giá cho sản phẩm ID: " + dbProduct.getProId() + ", size: " + cart.getId().getSize());
                    continue;
                }

                OrderDetailKey orderDetailKey = new OrderDetailKey(savedOrder.getOrderId(), dbProduct.getProId(), cart.getId().getSize());
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setId(orderDetailKey);
                orderDetail.setOrder(savedOrder); // Liên kết với Order vừa lưu
                orderDetail.setProduct(dbProduct);
                orderDetail.setQuantity(cart.getQuantity());
                orderDetailService.save(orderDetail);
            }

            cartService.clearCart(user.getUserId());

            return ResponseEntity.ok("<html><body>" +
                    "<h3>Thanh toán thành công và đơn hàng đã được tạo!</h3>" +
                    "<p>Bạn sẽ được chuyển hướng về trang chủ sau 5 giây...</p>" +
                    "<script>setTimeout(function(){ window.location.href = '/'; }, 5000);</script>" +
                    "</body></html>");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Thanh toán thất bại!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }
    }

    @GetMapping("/cash-on-delivery")
    public ResponseEntity<?> processCashOnDelivery(
            @RequestParam String province,
            @RequestParam String city,
            @RequestParam String commune,
            @RequestParam String address,
            @RequestParam String shippingMethodId,
            @RequestParam(required = false) String promotionCode, // Thêm mã khuyến mãi
            HttpServletRequest request ) {

        User user = userService.getUserLogged();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("<html><body><h3>Bạn cần đăng nhập để đặt hàng!</h3><a href='/auth/login'>Đăng nhập</a></body></html>");
        }

        Integer shipIdInt = Integer.parseInt(shippingMethodId);
        Double serverCalculatedTotal = orderService.calculateGrandTotalFromServer(user, shipIdInt, promotionCode);

        if (serverCalculatedTotal <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Giỏ hàng trống hoặc tổng tiền không hợp lệ!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }

        String fullAddress = address + ", " + commune + ", " + city + ", " + province;
        ShipmentCompany shipment = shipmentCompanyService.findById(shipIdInt);
        if (shipment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Phương thức vận chuyển không hợp lệ!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }

        List<Cart> carts = cartService.findByUserId(user.getUserId());
        if (carts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<html><body><h3>Giỏ hàng của bạn đang trống!</h3><a href='/'>Quay lại trang chủ</a></body></html>");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCurrency("VND");
        order.setTotal(serverCalculatedTotal); // Sử dụng tổng tiền đã tính toán từ server
        order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        order.setPaymentMethod("CashOnDelivery");
        order.setStatus("Pending");
        order.setDeliAddress(fullAddress);
        order.setShipmentCompany(shipment);
        Order savedOrder = orderService.save(order);

        for (Cart cart : carts) {
            Product dbProduct = productService.findById(cart.getProduct().getProId());
            if (dbProduct == null || cart.getId() == null || cart.getId().getSize() == null) {
                System.err.println("Bỏ qua cart item không hợp lệ khi tạo order detail: " + cart);
                continue;
            }
            ProductDetail productDetail = productDetailService.findPriceByProductIdAndSize(
                    dbProduct.getProId(),
                    ProductAttribute.getEnum(cart.getId().getSize())
            );
            if (productDetail == null || productDetail.getPrice() == null) {
                System.err.println("Không tìm thấy giá cho sản phẩm ID: " + dbProduct.getProId() + ", size: " + cart.getId().getSize());
                continue;
            }

            OrderDetailKey orderDetailKey = new OrderDetailKey(savedOrder.getOrderId(), dbProduct.getProId(), cart.getId().getSize());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(orderDetailKey);
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(dbProduct);
            orderDetail.setQuantity(cart.getQuantity());
            orderDetailService.save(orderDetail);
        }

        cartService.clearCart(user.getUserId());
        return ResponseEntity.ok("<html><body>" +
                "<h3>Đặt hàng thành công (COD)! Đơn hàng của bạn đang được xử lý.</h3>" +
                "<p>Bạn sẽ được chuyển hướng về trang chủ sau 5 giây...</p>" +
                "<script>setTimeout(function(){ window.location.href = '/'; }, 5000);</script>" +
                "</body></html>");
    }
}