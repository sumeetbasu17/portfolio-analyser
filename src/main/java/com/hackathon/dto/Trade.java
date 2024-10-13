package com.hackathon.dto;

import java.time.LocalDate;

public class Trade {
    private String symbol;
    private int quantity;
    private LocalDate tradeDate;
    private String action; // e.g., "buy" or "sell"
    private double averagePrice;
    private String sector; // New field for the sector

    public Trade(String symbol, int quantity, LocalDate tradeDate, String action, double averagePrice, String sector) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.tradeDate = tradeDate;
        this.action = action;
        this.averagePrice = averagePrice;
        this.sector = sector; // Initialize the sector
    }

    // Getters and Setters

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getSector() {
        return sector; // Getter for sector
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
