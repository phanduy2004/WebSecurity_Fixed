package com.group8.alomilktea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "[shipment_company]") // Changed to "users" to avoid conflicts with SQL reserved keyword
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentCompany {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ship_cid")
    private Integer shipCid;

    @Column(name ="methodname")
    private String shipCname;

    @Column(name = "price")
    private Double price;

    @OneToMany(mappedBy = "shipmentCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_ShipmentCompany_User"))
    private User user;


}
