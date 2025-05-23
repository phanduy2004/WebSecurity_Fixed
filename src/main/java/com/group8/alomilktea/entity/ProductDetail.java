package com.group8.alomilktea.entity;

import com.group8.alomilktea.common.enums.ProductAttribute;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_detail") // Đảm bảo bảng trong DB có tên là "product"
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private Integer proDId;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private ProductAttribute size;

    @Column(name = "price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "pro_id", referencedColumnName = "pro_id", foreignKey = @ForeignKey(name = "FK_Product_ProducDelail"))
    @ToString.Exclude // Bỏ trường này khi gọi toString()
    private Product product;

    @Override
    public String toString() {
        return "ProductDetail{" +
                "price=" + price +
                ", proDId=" + proDId +
                ", size=" + size +
                ", product=" + product +
                '}';
    }
}
