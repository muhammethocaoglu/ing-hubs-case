package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.service.StockExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/stock-exchange")
@RequiredArgsConstructor
public class StockExchangeController {

    private final StockExchangeService stockExchangeService;

    @PostMapping("")
    public ResponseEntity<CreateStockExchangeResponse> create(@Valid @RequestBody CreateStockExchangeRequest createStockExchangeRequest) {
        return new ResponseEntity<>(stockExchangeService.create(createStockExchangeRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{name}")
    public ResponseEntity<Void> addStock(@PathVariable(value = "name") String stockExchangeName,
                                         @Valid @RequestBody AddStockRequest addStockRequest) {
        stockExchangeService.addStock(stockExchangeName, addStockRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteStock(@PathVariable(value = "name") String stockExchangeName,
                                            @Valid @RequestBody DeleteStockFromStockExchangeRequest deleteStockFromStockExchangeRequest) {
        stockExchangeService.deleteStock(stockExchangeName, deleteStockFromStockExchangeRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<ListStockResponseItem>> listStocks(@PathVariable(value = "name") String stockExchangeName) {
        return new ResponseEntity<>(stockExchangeService.listStocks(stockExchangeName), HttpStatus.OK);
    }
}
