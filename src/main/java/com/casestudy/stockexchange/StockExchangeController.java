package com.casestudy.stockexchange;

import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.repository.StockExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/stock-exchange")
public class StockExchangeController {

    @Autowired
    StockExchangeRepository stockExchangeRepository;

    @PostMapping("")
    public ResponseEntity<StockExchange> create(@RequestBody StockExchange stockExchange) {
        StockExchange stockExchangeCreated = stockExchangeRepository.save(StockExchange.builder()
                .name("testexchange")
                .description("testexchange description")
                .liveInMarket(false)
                .build());
        return new ResponseEntity<>(stockExchangeCreated, HttpStatus.CREATED);
    }
}

