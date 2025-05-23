package com.group8.alomilktea.vnpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public abstract class PaymentDTO {
    @Builder
    public static class VNPayResponse {

        public String code;

        public String message;

        public String ketao;
    }
}
