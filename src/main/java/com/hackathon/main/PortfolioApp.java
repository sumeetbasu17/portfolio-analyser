package com.hackathon.main;

import java.io.IOException;
import java.util.List;

import org.bson.Document;

import com.hackathon.client.AlpacaClient;
import com.hackathon.dto.Portfolio;
import com.hackathon.repository.PortfolioRepository;
import com.hackathon.service.PortfolioService;
import com.hackathon.service.SectorService;
import com.hackathon.service.VectorSearchService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class PortfolioApp {
    public static void main(String[] args) {
        // Initialize MongoDB client and database
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017"); // Adjust connection string as needed
        MongoDatabase database = mongoClient.getDatabase("your_database_name"); // Replace with your database name
        SectorService sectorService= new SectorService(database);
        VectorSearchService vectorSearchService = new VectorSearchService(database);

        // Initialize dependencies
        AlpacaClient alpacaClient = new AlpacaClient();
        PortfolioRepository portfolioRepository = new PortfolioRepository(database); // Assuming it requires the database
        PortfolioService portfolioService = new PortfolioService(portfolioRepository, alpacaClient, sectorService, vectorSearchService);

        String userId = "user123";

        try {
            // Step 1: Aggregate portfolio data and save to MongoDB
            portfolioService.aggregatePortfolioData(userId);

            // Step 2: Retrieve the saved portfolio
            Portfolio portfolio = portfolioService.getPortfolioByUserId(userId);

            // Step 3: Search for similar portfolios
            List<Document> similarPortfolios = vectorSearchService.searchSimilarPortfolios(portfolio.getVector(), 5);

            // Display similar portfolios
            System.out.println("Similar Portfolios:");
            for (Document doc : similarPortfolios) {
                System.out.println(doc.toJson());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close(); // Close the MongoDB client connection
        }
    }
}
