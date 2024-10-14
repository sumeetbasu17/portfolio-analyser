package com.hackathon.service;

import smile.clustering.KMeans;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LinearModel;
import smile.regression.OLS;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hackathon.client.AlpacaClient;
import com.hackathon.dto.Trade;

public class RecommendationService {

    private AlpacaClient alpacaClient;
    private PortfolioService portfolioService;

    public RecommendationService (AlpacaClient alpacaClient, PortfolioService portfolioService) {
        this.alpacaClient = alpacaClient;
        this.portfolioService = portfolioService;
    }

    public String provideInvestmentRecommendations(String userId) {
        List<Trade> trades = portfolioService.getPortfolioByUserId(userId).getTrades();
        // Predict portfolio growth using the trained model
        double predictedGrowth = predictPortfolioGrowth(trades);

        // Run k-means clustering for portfolio diversification recommendations
        String diversificationStrategy = performClustering(trades);

        // Combine results into a recommendation
        return generateRecommendation(predictedGrowth, diversificationStrategy);
    }

    // Generate the final recommendation based on predicted growth and clustering strategy
    private String generateRecommendation(double predictedGrowth, String diversificationStrategy) {
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("Predicted Portfolio Growth: ").append(predictedGrowth).append("\n");
        recommendation.append("Diversification Strategy: ").append(diversificationStrategy);
        return recommendation.toString();
    }

    // Method to perform KMeans clustering
    private String performClustering(List<Trade> trades) {
        double[][] features = prepareFeatures(trades); // Cluster based on historical return and volatility
        int k = 3;  // Example: suggesting 3 clusters for diversification

        // Fit the KMeans model
        KMeans kmeans = KMeans.fit(features, k);

        // Group trades based on cluster assignments
        List<List<Trade>> clusteredTrades = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            clusteredTrades.add(new ArrayList<>());
        }

        for (int i = 0; i < trades.size(); i++) {
            int clusterIndex = kmeans.y[i]; // Get cluster index for the trade
            clusteredTrades.get(clusterIndex).add(trades.get(i)); // Add trade to the respective cluster
        }

        // Generate diversification strategy based on clusters
        StringBuilder diversificationStrategy = new StringBuilder();
        diversificationStrategy.append("Based on clustering, diversify into the following ")
                .append(k)
                .append(" groups of stocks:\n");

        for (int i = 0; i < clusteredTrades.size(); i++) {
            diversificationStrategy.append("Group ")
                    .append(i + 1)
                    .append(": ")
                    .append(clusteredTrades.get(i).stream()
                            .map(Trade::getSymbol)
                            .collect(Collectors.joining(", "))) // Get symbols of stocks in the group
                    .append("\n");
        }

        return diversificationStrategy.toString();
    }

    // Predict portfolio growth based on trades
    public double predictPortfolioGrowth(List<Trade> trades) {
        double[][] features = prepareFeatures(trades);
        double[] growthLabels = prepareGrowthLabels(trades);

        // Create DataFrame for features
        DataFrame featureDataFrame = DataFrame.of(features, "Return", "Volatility");

        // Convert growthLabels to a 2D array for DataFrame creation
        double[][] labelArray = new double[growthLabels.length][1]; // 2D array
        for (int i = 0; i < growthLabels.length; i++) {
            labelArray[i][0] = growthLabels[i];
        }

        // Create DataFrame for growth labels
        DataFrame labelDataFrame = DataFrame.of(labelArray, "Growth");

        // Combine features and labels into one DataFrame
        DataFrame combinedData = featureDataFrame.merge(labelDataFrame);

        // Fit OLS model using growth as the dependent variable
        LinearModel olsModel = OLS.fit(Formula.lhs("Growth"), combinedData);

        // Prepare current features for prediction based on the latest trades
        double[] currentFeatures = prepareCurrentFeatures(trades);

        // Make a prediction using the fitted model
        return olsModel.predict(currentFeatures);
    }

    // Prepare features (historical returns and volatility) for training
    private double[][] prepareFeatures(List<Trade> trades) {
        double[][] features = new double[trades.size()][2]; // Assuming 2 features: return and volatility
        for (int i = 0; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            features[i][0] = fetchHistoricalReturn(trade.getSymbol()); // Historical return
            features[i][1] = fetchHistoricalVolatility(trade.getSymbol()); // Historical volatility
        }
        return features;
    }

    // Prepare growth labels based on historical data
    private double[] prepareGrowthLabels(List<Trade> trades) {
        double[] growthLabels = new double[trades.size()];
        for (int i = 0; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            growthLabels[i] = calculateHistoricalGrowth(trade.getSymbol());
        }
        return growthLabels;
    }

    // Prepare current features for prediction
    private double[] prepareCurrentFeatures(List<Trade> trades) {
        // Assuming we're using the first trade's symbol for current features
        double[] currentFeatures = new double[2];
        currentFeatures[0] = fetchCurrentReturn(trades.get(0).getSymbol()); // Current return
        currentFeatures[1] = fetchCurrentVolatility(trades.get(0).getSymbol()); // Current volatility
        return currentFeatures;
    }

    // Fetch historical return for a symbol
    private double fetchHistoricalReturn(String symbol) {
        try {
            double priceAtStart = alpacaClient.getHistoricalPrice(symbol, "1Y");
            double priceAtEnd = alpacaClient.getCurrentPrice(symbol);
            return (priceAtEnd - priceAtStart) / priceAtStart;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Fetch historical volatility for a symbol
    private double fetchHistoricalVolatility(String symbol) {
        try {
            List<Double> historicalPrices = alpacaClient.getHistoricalPrices(symbol, "1Y");
            return calculateVolatility(historicalPrices);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Calculate historical growth for a symbol
    private double calculateHistoricalGrowth(String symbol) {
        try {
            double priceAtStart = alpacaClient.getHistoricalPrice(symbol, "1Y");
            double priceAtEnd = alpacaClient.getCurrentPrice(symbol);
            return (priceAtEnd - priceAtStart) / priceAtStart;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Fetch current return for a symbol
    private double fetchCurrentReturn(String symbol) {
        try {
            double priceOneWeekAgo = alpacaClient.getHistoricalPrice(symbol, "1W");
            double currentPrice = alpacaClient.getCurrentPrice(symbol);
            return (currentPrice - priceOneWeekAgo) / priceOneWeekAgo;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Fetch current volatility for a symbol
    private double fetchCurrentVolatility(String symbol) {
        try {
            List<Double> recentPrices = alpacaClient.getHistoricalPrices(symbol, "1M");
            return calculateVolatility(recentPrices);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Calculate portfolio volatility by averaging volatility of individual stocks
    public double calculatePortfolioVolatility(List<Trade> trades) {
        double totalVolatility = 0.0;
        for (Trade trade : trades) {
            totalVolatility += fetchCurrentVolatility(trade.getSymbol());
        }
        return totalVolatility / trades.size();  // Average volatility
    }

    // Helper method to calculate volatility from price data
    private double calculateVolatility(List<Double> prices) {
        if (prices.size() < 2) return 0.0;

        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double priceReturn = (prices.get(i) - prices.get(i - 1)) / prices.get(i - 1);
            returns.add(priceReturn);
        }

        double meanReturn = returns.stream().mapToDouble(val -> val).average().orElse(0.0);
        double variance = returns.stream().mapToDouble(val -> Math.pow(val - meanReturn, 2)).sum() / returns.size();
        return Math.sqrt(variance);
    }
}

