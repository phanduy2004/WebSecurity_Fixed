    package com.group8.alomilktea.entity;

    import jakarta.persistence.*;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "product") // Đảm bảo bảng trong DB có tên là "product"
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "pro_id")
        private Integer proId;

        @Column(name = "name", nullable = false, length = 1000)
        private String name;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "promotion_id", referencedColumnName = "promotion_id",foreignKey = @ForeignKey(name = "FK_Product_Promotion") ) // tham chiếu đến promotionId của bảng Promotion
        private Promotion promotion;

        @Column(name = "image_link", length = 1000)
        private String imageLink;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cate_id", referencedColumnName = "cate_id")
        @ToString.Exclude // Bỏ trường này khi gọi toString()
        private Category category; // Đảm bảo bạn có thuộc tính category trong Product


        // lấy list productdetail của product
        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        @ToString.Exclude // Bỏ trường này khi gọi toString()
        private List<ProductDetail> productDetails = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        @ToString.Exclude // Bỏ trường này khi gọi toString()
        private List<OrderDetail> orderDetails = new ArrayList<>();

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        @ToString.Exclude // Bỏ trường này khi gọi toString()
        private List<Rating> ratings = new ArrayList<>();

        public boolean isPresent() {
            return this.proId != null;
        }
    }
