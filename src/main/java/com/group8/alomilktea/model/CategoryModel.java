package com.group8.alomilktea.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryModel {
    private Integer cateId;
    private String name;
    private String description;
    private String logo;

    public CategoryModel(Integer cateId, String name) {
        this.cateId = cateId;
        this.name = name;
    }
}
