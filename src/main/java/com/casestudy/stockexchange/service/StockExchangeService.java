package com.casestudy.stockexchange.service;

import com.casestudy.stockexchange.controller.dto.*;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.exception.EntityAlreadyExistsException;
import com.casestudy.stockexchange.exception.ResourceNotFoundException;
import com.casestudy.stockexchange.repository.StockExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockExchangeService {
    private final StockExchangeRepository stockExchangeRepository;
    private final StockService stockService;

    public CreateStockExchangeResponse create(CreateStockExchangeRequest createStockExchangeRequest) {
        stockExchangeRepository.findByName(createStockExchangeRequest.getName())
                .ifPresent(stockExchange -> {
                    throw new EntityAlreadyExistsException(String.format("Stock exchange with name %s already exists",
                            createStockExchangeRequest.getName()));
                });

        StockExchange stockExchangeCreated = stockExchangeRepository.save(StockExchange.builder()
                .name(createStockExchangeRequest.getName())
                .description(createStockExchangeRequest.getDescription())
                .liveInMarket(false)
                .stocks(new HashSet<>())
                .build());
        return CreateStockExchangeResponse.builder()
                .id(stockExchangeCreated.getId())
                .name(stockExchangeCreated.getName())
                .description(stockExchangeCreated.getDescription())
                .liveInMarket(stockExchangeCreated.getLiveInMarket())
                .build();
    }

    public void addStock(String stockExchangeName, AddStockRequest addStockRequest) {
        StockExchange stockExchange = getByName(stockExchangeName);
        Stock stock = stockService.getById(addStockRequest.getStockId());
        stockExchange.addStock(stock);
        stockExchangeRepository.save(stockExchange);
    }

    public void deleteStock(String stockExchangeName, DeleteStockFromStockExchangeRequest deleteStockFromStockExchangeRequest) {
        StockExchange stockExchange = getByName(stockExchangeName);
        Stock stock = stockService.getById(deleteStockFromStockExchangeRequest.getStockId());
        stockExchange.removeStock(stock.getId());
        stockExchangeRepository.save(stockExchange);
    }

    public List<ListStockResponseItem> listStocks(String stockExchangeName) {
        StockExchange stockExchange = getByName(stockExchangeName);
        List<Stock> stockList = stockService.getAllByStockExchangeId(stockExchange.getId());
        return stockList.stream()
                .map(stock -> ListStockResponseItem.builder()
                        .id(stock.getId())
                        .name(stock.getName())
                        .description(stock.getDescription())
                        .currentPrice(stock.getCurrentPrice())
                        .lastUpdate(stock.getLastUpdate())
                        .build())
                .toList();
    }

    private StockExchange getByName(String stockExchangeName) {
        return stockExchangeRepository.findByName(stockExchangeName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Stock exchange with name %s not found",
                        stockExchangeName)));
    }

}
