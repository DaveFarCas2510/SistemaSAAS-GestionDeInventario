package com.saas.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class StockMovementDTO {
        private String type;
        private int quantity;
        private int previousStock;
        private int newStock;
        private String username;
        private LocalDateTime date;
}
