package com.group8.alomilktea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailKey {
    private Integer order_id;
    private Integer pro_id;
    private String size;
    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailKey that = (OrderDetailKey) o;
        return Objects.equals(order_id, that.order_id) && Objects.equals(pro_id, that.pro_id) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order_id, pro_id,size);
    }
}
