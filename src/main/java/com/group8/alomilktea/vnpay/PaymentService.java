package com.group8.alomilktea.vnpay;


import com.group8.alomilktea.config.payment.VNPAYConfig;
import com.group8.alomilktea.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    public long amount =0;
    public String fullAddress;
    public String shipingid;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        String amountParam = request.getParameter("grandTotalAmount");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String commune = request.getParameter("commune");
        String address = request.getParameter("address");
        shipingid = request.getParameter("shippingMethodId");
        System.out.println("đuyeptrai" +shipingid);
        // Xây dựng địa chỉ đầy đủ
        fullAddress = address + ", " + commune + ", " + city + ", " + province;
        if (amountParam == null || amountParam.isEmpty()) {
            throw new IllegalArgumentException("Amount parameter is required and cannot be null or empty");
        }

        double amountDouble = Double.parseDouble(amountParam);
        amount = (long) (amountDouble * 100);
        System.out.println("amount is " + amount);

        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
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
