package com.casestudy.stockexchange.controller.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GetStockExchangeWithStocksResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean liveInMarket;
    private List<ListStockResponseItem> stocks;
}
