package com.group8.alomilktea.vnpay;

import com.group8.alomilktea.config.payment.VNPAYConfig;
import com.group8.alomilktea.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID; // For unique transaction reference

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    // Loại bỏ các biến instance: amount, fullAddress, shipingid

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request,
                                                       Double serverCalculatedTotalAmount,
                                                       String orderTxnRef, // Mã giao dịch đơn hàng duy nhất
                                                       String orderInfo) { // Thông tin đơn hàng
        if (serverCalculatedTotalAmount == null || serverCalculatedTotalAmount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        long amountInCents = (long) (serverCalculatedTotalAmount * 100);

        String bankCode = request.getParameter("bankCode"); // Bank code có thể vẫn lấy từ request
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();

        // Ghi đè/thêm các giá trị cần thiết
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountInCents));
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo", orderInfo); // Sử dụng thông tin đơn hàng truyền vào
        vnpParamsMap.put("vnp_TxnRef", orderTxnRef); // Sử dụng mã giao dịch duy nhất

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("Redirecting to VNPay")
                .ketao(paymentUrl)
                .build();
    }
}