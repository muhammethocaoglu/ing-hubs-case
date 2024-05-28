package com.casestudy.stockexchange.repository;

import com.casestudy.stockexchange.entity.StockExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockExchangeRepository extends JpaRepository<StockExchange, Long> {
}
