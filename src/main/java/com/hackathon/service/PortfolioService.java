package com.hackathon.service;

import com.hackathon.client.AlpacaClient;
import com.hackathon.dto.Portfolio;
import com.hackathon.dto.Trade;
import com.hackathon.repository.PortfolioRepository;
import java.io.IOException;
import java.util.List;

import org.bson.Document;

public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AlpacaClient alpacaClient;
    private final SectorService sectorService;
    private final VectorSearchService vectorSearchService;

    public PortfolioService(PortfolioRepository portfolioRepository, 
                            AlpacaClient alpacaClient, 
                            SectorService sectorService,
                            VectorSearchService vectorSearchService) {
        this.portfolioRepository = portfolioRepository;
        this.alpacaClient = alpacaClient;
        this.sectorService = sectorService;
        this.vectorSearchService = vectorSearchService;
    }

    /**
     * Aggregates portfolio data for the given user ID.
     *
     * @param userId The user ID for which to aggregate portfolio data.
     * @throws IOException If there is an error fetching positions from Alpaca.
     */
    public void aggregatePortfolioData(String userId) throws IOException {
        List<Trade> trades = alpacaClient.getPositions();

        // Fetch sector information for each trade
        for (Trade trade : trades) {
            String sector = sectorService.fetchSectorFromAPI(trade.getSymbol());
            trade.setSector(sector);
        }
        portfolioRepository.saveTrades(userId, trades);
    }

    /**
     * Retrieves a user's portfolio based on their user ID.
     *
     * @param userId The user ID whose portfolio is to be retrieved.
     * @return The Portfolio object containing trade data.
     */
    public Portfolio getPortfolioByUserId(String userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public List<Document> findSimilarPortfolios(String userId, int topN) {
        // Fetch trades from the database using the userId
        List<Trade> trades = getPortfolioByUserId(userId).getTrades();        
        // Check if there are no trades for the user
        if (trades.isEmpty()) {
            return List.of(); // Return empty list if no trades found
        }
        // Generate portfolio vector from trades
        PortfolioVectorGenerator portfolioVectorGenerator = new PortfolioVectorGenerator(sectorService);
        List<Double> portfolioVector = portfolioVectorGenerator.generatePortfolioVector(trades);
        // Use VectorSearchService to find similar portfolios
        return vectorSearchService.searchSimilarPortfolios(portfolioVector, topN);
    }
}
