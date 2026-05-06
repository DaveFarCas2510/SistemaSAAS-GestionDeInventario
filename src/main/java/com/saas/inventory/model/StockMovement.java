package com.saas.inventory.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // ENTRY o EXIT

    private int quantity;

    private int previousStock;

    private int newStock;

    private LocalDateTime date;

    private String username;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
