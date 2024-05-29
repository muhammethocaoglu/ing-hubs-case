package com.casestudy.stockexchange.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateStockRequest {
    private String name;
    private String description;
    private Double currentPrice;
}
