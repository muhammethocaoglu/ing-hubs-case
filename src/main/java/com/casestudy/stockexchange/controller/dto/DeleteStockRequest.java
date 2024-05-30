package com.casestudy.stockexchange.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeleteStockRequest {
    @NotNull(message = "Id is required")
    private Long id;
}
