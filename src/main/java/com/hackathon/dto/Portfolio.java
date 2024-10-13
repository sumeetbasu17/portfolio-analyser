package com.hackathon.dto;

import java.time.LocalDateTime;
import java.util.List;

public class Portfolio {
    private String userId;
    private List<Trade> trades;
    private List<Double> vector;
    private LocalDateTime lastUpdated;

    public Portfolio(String userId, List<Trade> trades) {
        this.userId = userId;
        this.trades = trades;
    }

    public Portfolio(String userId, List<Trade> trades, List<Double> vector, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.trades = trades;
        this.vector = vector;
        this.lastUpdated = lastUpdated;
    }

    public String getUserId() {
        return userId;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public List<Double> getVector() {
        return vector;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
