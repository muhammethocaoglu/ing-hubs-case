package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.repository.StockRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;


@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockRepository stockRepository;

    @PostMapping("")
    public ResponseEntity<CreateStockResponse> create(@Valid @RequestBody CreateStockRequest createStockRequest) {
        Stock stockCreated = stockRepository.save(Stock.builder()
                .name(createStockRequest.getName())
                .description(createStockRequest.getDescription())
                .currentPrice(createStockRequest.getCurrentPrice())
                .lastUpdate(Instant.now().toEpochMilli())
                .stockExchanges(new HashSet<>())
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
    public ResponseEntity<Void> updateCurrentPrice(@Valid @RequestBody UpdateStockPriceRequest updateStockPriceRequest) {
        Stock stock = stockRepository.findById(updateStockPriceRequest.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        updateStockPriceRequest.getId())));
        stock.setCurrentPrice(updateStockPriceRequest.getCurrentPrice());
        stock.setLastUpdate(Instant.now().toEpochMilli());
        stockRepository.save(stock);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteStockRequest deleteStockRequest) {
        Stock stock = stockRepository.findById(deleteStockRequest.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        deleteStockRequest.getId())));
        for (StockExchange stockExchange : stock.getStockExchanges()) {
            stockExchange.getStocks().remove(stock);
            stockExchange.updateLiveInMarket();
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

