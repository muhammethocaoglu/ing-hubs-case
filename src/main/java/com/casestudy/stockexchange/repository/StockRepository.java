package com.casestudy.stockexchange.repository;

import com.casestudy.stockexchange.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findStocksByStockExchangesId(Long stockExchangeId);
}
