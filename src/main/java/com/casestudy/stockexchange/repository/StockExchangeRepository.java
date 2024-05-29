package com.casestudy.stockexchange.repository;

import com.casestudy.stockexchange.entity.StockExchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockExchangeRepository extends JpaRepository<StockExchange, Long> {
    Optional<StockExchange> findByName(String name);
}
