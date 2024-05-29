package com.casestudy.stockexchange.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GetStockResponse {
    private Long id;
    private String name;
    private String description;
    private Double currentPrice;
    private Long lastUpdate;
}
