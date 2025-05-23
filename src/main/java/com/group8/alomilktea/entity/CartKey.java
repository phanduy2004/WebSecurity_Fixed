package com.group8.alomilktea.entity;


import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartKey {
    private Integer user_id;
    private Integer pro_id;
    private String size;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartKey cartKey = (CartKey) o;
        return Objects.equals(user_id, cartKey.user_id) &&
                Objects.equals(pro_id, cartKey.pro_id) &&
                Objects.equals(size, cartKey.size);

    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, pro_id,size);
    }
}
