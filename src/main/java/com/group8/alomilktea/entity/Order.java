package com.group8.alomilktea.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")  // Đảm bảo tên bảng là "orders"
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_user_order"))
    @JsonIgnore
    private User user;

    @Column(name = "currency")
    private String currency;

    @Column(name = "date")
    private String date;

    @Column(name = "delivery_address")
    private String deliAddress;

    @Column(name = "payment_method", columnDefinition = "varchar(255) default 'COD'")
    private String paymentMethod;

    @Column(name = "status")
    private String status;

    @Column(name = "total")
    private Double total;

    @ManyToOne
    @JoinColumn(name = "ship_cid", referencedColumnName = "ship_cid", foreignKey = @ForeignKey(name = "FK_Ship_Oder"))
    private ShipmentCompany shipmentCompany;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
    public Order(String currency, String date, String paymentMethod, ShipmentCompany shipmentCompany,String status, Double total, User user) {
        this.currency = currency;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.shipmentCompany = shipmentCompany;
        this.status = status;
        this.total = total;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", currency='" + currency + '\'' +
                ", date='" + date + '\'' +
                ", deliAddress='" + deliAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", shipmentCompany=" + (shipmentCompany != null ? shipmentCompany.getShipCname() : null) +
                '}';
    }
    }
