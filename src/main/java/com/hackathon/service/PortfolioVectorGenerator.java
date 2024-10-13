package com.hackathon.service;

import java.io.IOException;
import java.util.*;
import com.hackathon.dto.Trade;

public class PortfolioVectorGenerator {

    private List<String> distinctSectors; // To store distinct sectors
    private SectorService sectorService;

    // Constructor to initialize SectorService
    public PortfolioVectorGenerator(SectorService sectorService) {
        distinctSectors = sectorService.getDistinctSectors(); // Fetch distinct sectors once
    }

    public List<Double> generatePortfolioVector(List<Trade> trades) {
        // Map to hold sector allocations
        Map<String, Double> sectorAllocations = new HashMap<>();
        double totalValue = 0.0;

        // Calculate total portfolio value
        for (Trade trade : trades) {
            totalValue += trade.getQuantity() * trade.getAveragePrice();
        }

        // Calculate sector allocations
        for (Trade trade : trades) {
            String sector = fetchSector(trade.getSymbol());
            double value = trade.getQuantity() * trade.getAveragePrice();
            sectorAllocations.put(sector, sectorAllocations.getOrDefault(sector, 0.0) + value);
        }

        // Normalize allocations to percentages
        List<Double> vector = new ArrayList<>();

        for (String sector : distinctSectors) {
            double allocation = sectorAllocations.getOrDefault(sector, 0.0) / totalValue;
            vector.add(allocation);
        }

        // Add risk metrics
        double volatility = calculateVolatility(trades);
        double sharpeRatio = calculateSharpeRatio(trades);

        vector.add(volatility);
        vector.add(sharpeRatio);

        return vector;
    }

    private String fetchSector(String symbol) {
        try {
            return sectorService.fetchSectorFromAPI(symbol); // Call the SectorService to fetch sector info
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown"; // Fallback sector if there is an error
        }
    }

    private static double calculateVolatility(List<Trade> trades) {
        double mean = calculateMean(trades);
        double sumSquaredDiffs = 0.0;
        int n = trades.size();

        for (Trade trade : trades) {
            double returnValue = (trade.getAveragePrice() * trade.getQuantity()) / mean;
            sumSquaredDiffs += Math.pow(returnValue - mean, 2);
        }

        return Math.sqrt(sumSquaredDiffs / n); // Sample standard deviation
    }

    private static double calculateSharpeRatio(List<Trade> trades) {
        double meanReturn = calculateMeanReturn(trades);
        double riskFreeRate = 0.01; // Example risk-free rate
        double volatility = calculateVolatility(trades);

        if (volatility == 0) return 0; // Avoid division by zero

        return (meanReturn - riskFreeRate) / volatility;
    }

    private static double calculateMean(List<Trade> trades) {
        double totalValue = 0.0;
        for (Trade trade : trades) {
            totalValue += trade.getQuantity() * trade.getAveragePrice();
        }
        return trades.size() > 0 ? totalValue / trades.size() : 0;
    }

    private static double calculateMeanReturn(List<Trade> trades) {
        double totalReturn = 0.0;
        for (Trade trade : trades) {
            totalReturn += trade.getAveragePrice() * trade.getQuantity(); // Placeholder for actual return calculation
        }
        return trades.size() > 0 ? totalReturn / trades.size() : 0;
    }
}
