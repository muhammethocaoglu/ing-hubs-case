package com.casestudy.stockexchange.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeleteStockFromStockExchangeRequest {
    @NotNull(message = "Stock id is required")
    private Long stockId;
}
