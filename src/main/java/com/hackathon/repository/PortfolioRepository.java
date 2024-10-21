package com.hackathon.repository;

import com.hackathon.dto.Portfolio;
import com.hackathon.dto.Trade;
import com.hackathon.service.PortfolioVectorGenerator;
import com.hackathon.service.SectorService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.stereotype.Repository;

@Repository
public class PortfolioRepository {

    private MongoCollection<Document> portfolioCollection;
    private SectorService sectorService;

    public PortfolioRepository(MongoDatabase database) {
        this.portfolioCollection = database.getCollection("portfolios");
    }

    public void saveTrades(String userId, List<Trade> trades) {
        List<Document> tradeDocuments = new ArrayList<>();
        
        // Prepare trade documents from Trade objects
        for (Trade trade : trades) {
            Document tradeDoc = new Document()
                .append("symbol", trade.getSymbol())
                .append("quantity", trade.getQuantity())
                .append("tradeDate", trade.getTradeDate())
                .append("buySell", trade.getAction())
                .append("sector", trade.getSector()); // Store sector information
    
            tradeDocuments.add(tradeDoc);
        }
        // Optional: Generate and save portfolio vector
        PortfolioVectorGenerator portfolioVectorGenerator = new PortfolioVectorGenerator(sectorService);
        List<Double> portfolioVector = portfolioVectorGenerator.generatePortfolioVector(trades);
    
        // Create a portfolio document to store in MongoDB
        Document portfolioDocument = new Document("userId", userId)
            .append("trades", tradeDocuments)
            .append("portfolioVector", portfolioVector);
        
        portfolioCollection.insertOne(portfolioDocument);
    }
    

    public Portfolio findByUserId(String userId) {
        List<Trade> trades = new ArrayList<>();
    
        // Query the collection to find the portfolio for the given userId
        Document query = new Document("userId", userId);
        Document userPortfolio = portfolioCollection.find(query).first();
    
        if (userPortfolio != null) {
            // Safely retrieve the list of trade documents
            List<Document> tradeDocuments = userPortfolio.getList("trades", Document.class);
    
            for (Document tradeDoc : tradeDocuments) {
                String symbol = tradeDoc.getString("symbol");
                int quantity = tradeDoc.getInteger("quantity");
                LocalDate tradeDate = LocalDate.parse(tradeDoc.getString("tradeDate")); // Convert String to LocalDate
                String action = tradeDoc.getString("buySell");
                String sector = tradeDoc.getString("sector");
    
                trades.add(new Trade(symbol, quantity, tradeDate, action, 0.0, sector)); // Assuming 0.0 for averagePrice
            }
        }
    
        return new Portfolio(userId, trades); // Return the Portfolio object
    }    
}
