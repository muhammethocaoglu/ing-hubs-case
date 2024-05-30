package com.casestudy.stockexchange.service;

import com.casestudy.stockexchange.controller.dto.CreateStockRequest;
import com.casestudy.stockexchange.controller.dto.CreateStockResponse;
import com.casestudy.stockexchange.controller.dto.GetStockResponse;
import com.casestudy.stockexchange.controller.dto.UpdateStockPriceRequest;
import com.casestudy.stockexchange.entity.Stock;
import com.casestudy.stockexchange.entity.StockExchange;
import com.casestudy.stockexchange.exception.ResourceNotFoundException;
import com.casestudy.stockexchange.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public CreateStockResponse create(CreateStockRequest createStockRequest) {
        Stock stockCreated = stockRepository.save(Stock.builder()
                .name(createStockRequest.getName())
                .description(createStockRequest.getDescription())
                .currentPrice(createStockRequest.getCurrentPrice())
                .lastUpdate(Instant.now().toEpochMilli())
                .stockExchanges(new HashSet<>())
                .build());
        return CreateStockResponse.builder()
                .id(stockCreated.getId())
                .name(stockCreated.getName())
                .description(stockCreated.getDescription())
                .currentPrice(stockCreated.getCurrentPrice())
                .lastUpdate(stockCreated.getLastUpdate())
                .build();
    }

    public GetStockResponse get(Long id) {
        Stock stock = getById(id);
        return GetStockResponse.builder()
                .id(id)
                .name(stock.getName())
                .description(stock.getDescription())
                .currentPrice(stock.getCurrentPrice())
                .lastUpdate(stock.getLastUpdate())
                .build();
    }

    public void delete(Long id) {
        Stock stock = getById(id);
        for (StockExchange stockExchange : stock.getStockExchanges()) {
            stockExchange.getStocks().remove(stock);
            stockExchange.updateLiveInMarket();
        }
        stockRepository.delete(stock);
    }

    public void updateCurrentPrice(UpdateStockPriceRequest updateStockPriceRequest) {
        Stock stock = getById(updateStockPriceRequest.getId());
        stock.setCurrentPrice(updateStockPriceRequest.getCurrentPrice());
        stock.setLastUpdate(Instant.now().toEpochMilli());
        stockRepository.save(stock);
    }

    Stock getById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Stock with id %s not found",
                        id)));
    }

    List<Stock> getAllByStockExchangeId(Long stockExchangeId){
        return stockRepository.findStocksByStockExchangesId(stockExchangeId);
    }
}
