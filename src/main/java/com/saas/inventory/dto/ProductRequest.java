package com.saas.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductRequest {

    @NotBlank
    private String name;

    @Positive
    private Double price;

    @Min(0)
    private Integer stock;

    @NotNull
    private Long categoryId;
}
