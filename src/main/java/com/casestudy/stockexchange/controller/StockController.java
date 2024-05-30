package com.casestudy.stockexchange.controller;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("")
    public ResponseEntity<CreateStockResponse> create(@Valid @RequestBody CreateStockRequest createStockRequest) {
        return new ResponseEntity<>(stockService.create(createStockRequest), HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateCurrentPrice(@Valid @RequestBody UpdateStockPriceRequest updateStockPriceRequest) {
        stockService.updateCurrentPrice(updateStockPriceRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteStockRequest deleteStockRequest) {
        stockService.delete(deleteStockRequest.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetStockResponse> get(@PathVariable Long id) {
        return new ResponseEntity<>(stockService.get(id), HttpStatus.OK);
    }
}

