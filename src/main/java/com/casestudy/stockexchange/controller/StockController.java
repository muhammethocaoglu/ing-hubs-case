package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("")
    public ResponseEntity<CreateStockResponse> updateCurrentPrice(@RequestBody UpdateStockPriceRequest updateStockPriceRequest) {
        Stock stock = stockRepository.findById(updateStockPriceRequest.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        updateStockPriceRequest.getId())));
        stock.setCurrentPrice(updateStockPriceRequest.getCurrentPrice());
        stockRepository.save(stock);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody DeleteStockRequest deleteStockRequest) {
        Stock stock = stockRepository.findById(deleteStockRequest.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        deleteStockRequest.getId())));
        for (StockExchange stockExchange : stock.getStockExchanges()) {
            stockExchange.getStocks().remove(stock);
        }

        stockRepository.delete(stock);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetStockResponse> get(@PathVariable Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        id)));
        return new ResponseEntity<>(GetStockResponse.builder()
                .id(id)
                .name(stock.getName())
                .description(stock.getDescription())
                .currentPrice(stock.getCurrentPrice())
                .lastUpdate(stock.getLastUpdate())
                .build(), HttpStatus.OK);
    }
}

