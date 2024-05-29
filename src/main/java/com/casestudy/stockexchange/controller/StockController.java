package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.CreateStockRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockResponse;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockRepository stockRepository;

    @PostMapping("")
    public ResponseEntity<CreateStockResponse> create(@RequestBody CreateStockRequest createStockRequest) {
        Stock stockCreated = stockRepository.save(Stock.builder()
                .name(createStockRequest.getName())
                .description(createStockRequest.getDescription())
                .currentPrice(createStockRequest.getCurrentPrice())
                .lastUpdate(Instant.now().toEpochMilli())
                .build());
        return new ResponseEntity<>(CreateStockResponse.builder()
                .id(stockCreated.getId())
                .name(stockCreated.getName())
                .description(stockCreated.getDescription())
                .currentPrice(stockCreated.getCurrentPrice())
                .lastUpdate(stockCreated.getLastUpdate())
                .build(), HttpStatus.CREATED);
    }
}

