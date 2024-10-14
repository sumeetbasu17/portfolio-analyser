package com.hackathon.service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PredefinedDataService {

    private MongoCollection<Document> portfolioCollection;

    public PredefinedDataService(MongoCollection<Document> portfolioCollection) {
        this.portfolioCollection = portfolioCollection;
    }

    public void insertPredefinedData() {
        List<Document> users = new ArrayList<>();

        // Predefined data for 10 users
        for (int i = 1; i <= 10; i++) {
            String userId = "user" + i;

            // Delete existing user portfolio if it exists
            deleteExistingPortfolio(userId);

            // Create new trades for the user
            List<Document> trades = new ArrayList<>();
            trades.add(createTrade("AAPL", "Technology", 10, 150.0, "BUY", "2024-01-01"));
            trades.add(createTrade("GOOG", "Technology", 5, 2800.0, "BUY", "2024-01-05"));
            trades.add(createTrade("JNJ", "Healthcare", 15, 160.0, "BUY", "2024-02-01"));
            trades.add(createTrade("JPM", "Finance", 20, 130.0, "BUY", "2024-03-01"));
            trades.add(createTrade("TSLA", "Technology", 3, 800.0, "BUY", "2024-04-01"));

            // Add portfolio document
            Document userPortfolio = new Document("userId", userId)
                    .append("trades", trades);

            users.add(userPortfolio);
        }

        // Insert all user portfolios into the database
        portfolioCollection.insertMany(users);
    }

    // Helper method to create a trade
    private Document createTrade(String symbol, String sector, int quantity, double averagePrice, String action, String tradeDate) {
        return new Document("symbol", symbol)
                .append("sector", sector)
                .append("quantity", quantity)
                .append("averagePrice", averagePrice)
                .append("action", action)
                .append("tradeDate", tradeDate);
    }

    // Helper method to delete the existing portfolio of a user
    private void deleteExistingPortfolio(String userId) {
        portfolioCollection.deleteOne(new Document("userId", userId));
    }
}
