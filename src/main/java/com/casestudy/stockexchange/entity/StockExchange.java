package com.casestudy.stockexchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stock_exchange")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean liveInMarket;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "stock_exchange_stocks",
            joinColumns = {@JoinColumn(name = "stock_exchange_id")},
            inverseJoinColumns = {@JoinColumn(name = "stock_id")})
    private Set<Stock> stocks = new HashSet<>();

}
