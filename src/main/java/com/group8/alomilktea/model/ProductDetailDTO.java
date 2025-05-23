package com.group8.alomilktea.model;

import com.group8.alomilktea.common.enums.ProductAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO { // Sử dụng "class" thay vì định nghĩa constructor
    private Integer proId;
    private String name;
    private String description;
    private String imageLink;
    private Integer cateId;
    private Integer proDId;
    private ProductAttribute size; // Enum
    private Double price;

    @Override
    public String toString() {
        return "ProductDetailDTO{" +
                "cateId=" + cateId +
                ", proId=" + proId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", proDId=" + proDId +
                ", size=" + size +
                ", price=" + price +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getProDId() {
        return proDId;
    }

    public void setProDId(Integer proDId) {
        this.proDId = proDId;
    }

    public Integer getProId() {
        return proId;
    }

    public void setProId(Integer proId) {
        this.proId = proId;
    }

    public ProductAttribute getSize() {
        return size;
    }

    public void setSize(ProductAttribute size) {
        this.size = size;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }
}
