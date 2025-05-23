package com.group8.alomilktea.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "wishlist")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CartKey id;  // Ánh xạ CartKey vào cơ sở dữ liệu, không cần trường `size` riêng biệt.

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private Double price;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pro_id", insertable = false, updatable = false)
    private Product product;

    public Wishlist(CartKey id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

}
