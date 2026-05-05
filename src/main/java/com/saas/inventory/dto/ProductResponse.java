package com.saas.inventory.dto;


import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String category;

    public ProductResponse(Long id, String name, Double price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}
