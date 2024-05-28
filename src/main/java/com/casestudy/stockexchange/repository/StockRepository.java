package com.casestudy.stockexchange.repository;

import com.casestudy.stockexchange.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
