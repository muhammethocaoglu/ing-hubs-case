package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.repository.StockExchangeRepository;
import com.casestudy.stockexchange.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/stock-exchange")
@RequiredArgsConstructor
public class StockExchangeController {

    private final StockExchangeRepository stockExchangeRepository;
    private final StockRepository stockRepository;

    @PostMapping("")
    public ResponseEntity<CreateStockExchangeResponse> create(@RequestBody CreateStockExchangeRequest createStockExchangeRequest) {
        stockExchangeRepository.findByName(createStockExchangeRequest.getName())
                .ifPresent(stockExchange -> {
                    throw new RuntimeException(String.format("Stock exchange with name %s already exists with id %s",
                            createStockExchangeRequest.getName(), stockExchange.getId()));
                });

        StockExchange stockExchangeCreated = stockExchangeRepository.save(StockExchange.builder()
                .name(createStockExchangeRequest.getName())
                .description(createStockExchangeRequest.getDescription())
                .liveInMarket(false)
                .build());
        return new ResponseEntity<>(CreateStockExchangeResponse.builder()
                .id(stockExchangeCreated.getId())
                .name(stockExchangeCreated.getName())
                .description(stockExchangeCreated.getDescription())
                .liveInMarket(stockExchangeCreated.getLiveInMarket())
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{name}")
    public ResponseEntity<Void> addStock(@PathVariable(value = "name") String stockExchangeName,
                                         @RequestBody AddStockRequest addStockRequest) {
        StockExchange stockExchange = stockExchangeRepository.findByName(stockExchangeName)
                .orElseThrow(() -> new RuntimeException(String.format("Stock exchange with name %s not found", stockExchangeName)));
        Stock stock = stockRepository.findById(addStockRequest.getStockId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        addStockRequest.getStockId())));
        stockExchange.addStock(stock);
        stockExchangeRepository.save(stockExchange);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteStock(@PathVariable(value = "name") String stockExchangeName,
                                            @RequestBody DeleteStockFromStockExchangeRequest deleteStockFromStockExchangeRequest) {
        StockExchange stockExchange = stockExchangeRepository.findByName(stockExchangeName)
                .orElseThrow(() -> new RuntimeException(String.format("Stock exchange with name %s not found", stockExchangeName)));
        Stock stock = stockRepository.findById(deleteStockFromStockExchangeRequest.getStockId())
                .orElseThrow(() -> new RuntimeException(String.format("Stock with id %s not found",
                        deleteStockFromStockExchangeRequest.getStockId())));
        stockExchange.removeStock(stock.getId());
        stockExchangeRepository.save(stockExchange);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<ListStockResponseItem>> listStocks(@PathVariable(value = "name") String stockExchangeName) {
        StockExchange stockExchange = stockExchangeRepository.findByName(stockExchangeName)
                .orElseThrow(() -> new RuntimeException(String.format("Stock exchange with name %s not found", stockExchangeName)));
        List<Stock> stockList = stockRepository.findStocksByStockExchangesId(stockExchange.getId());
        return new ResponseEntity<>(stockList.stream()
                .map(stock -> ListStockResponseItem.builder()
                        .id(stock.getId())
                        .name(stock.getName())
                        .description(stock.getDescription())
                        .currentPrice(stock.getCurrentPrice())
                        .lastUpdate(stock.getLastUpdate())
                        .build())
                .toList(), HttpStatus.OK);
    }
}

