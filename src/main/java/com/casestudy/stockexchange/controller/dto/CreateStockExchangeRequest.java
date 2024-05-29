package com.casestudy.stockexchange.controller.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateStockExchangeRequest {
    private String name;
    private String description;
}
