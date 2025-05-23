package com.group8.alomilktea.common.enums;

import lombok.Getter;

@Getter
public enum ProductAttribute {
    S("S"),
    M("M"),
    L("L");


    private final String size;

    ProductAttribute(String size) {
        this.size = size;
    }

    public static ProductAttribute getEnum(String size) {
        for (ProductAttribute v : values())
            if (v.getSize().equals(size)) return v;
        throw new IllegalArgumentException();
    }

}
