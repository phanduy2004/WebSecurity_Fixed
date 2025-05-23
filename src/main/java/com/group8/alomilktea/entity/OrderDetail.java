package com.group8.alomilktea.entity;

import com.group8.alomilktea.entity.Order;
import com.group8.alomilktea.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetail {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private OrderDetailKey id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_OrderDetail_Order"))
    private Order order; // Liên kết với Order

    @ManyToOne
    @JoinColumn(name = "pro_id", referencedColumnName = "pro_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_OrderDetail_Product"))
    private Product product; // Liên kết với Product

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Constructors, getters, and setters
}
