package com.group8.alomilktea.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
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

    public Cart(CartKey id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }


// Getters and Setters


}
