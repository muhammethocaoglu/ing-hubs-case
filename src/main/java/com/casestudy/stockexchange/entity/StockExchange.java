package com.casestudy.stockexchange.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stock_exchange")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
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

    public void addStock(Stock stock) {
        this.stocks.add(stock);
        stock.getStockExchanges().add(this);
        updateLiveInMarket();
    }

    public void removeStock(Long stockId) {
        Stock stock = this.stocks.stream().filter(s -> s.getId().equals(stockId)).findFirst().orElse(null);
        if (stock != null) {
            this.stocks.remove(stock);
            updateLiveInMarket();
            stock.getStockExchanges().remove(this);
        }
    }

    public void updateLiveInMarket() {
        this.liveInMarket = this.stocks.size() >= 5;
    }

}
