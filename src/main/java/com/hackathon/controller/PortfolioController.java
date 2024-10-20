package com.hackathon.controller;

import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hackathon.dto.Portfolio;
import com.hackathon.service.PortfolioService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Endpoint to trigger portfolio data aggregation for a user.
     *
     * @param userId The user ID for which to aggregate portfolio data.
     * @return HTTP response with success or error message.
     */
    @PostMapping("/aggregate/{userId}")
    public ResponseEntity<String> aggregatePortfolio(@PathVariable String userId) {
        try {
            portfolioService.aggregatePortfolioData(userId);
            return ResponseEntity.ok("Portfolio data aggregated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error aggregating portfolio data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve a user's portfolio.
     *
     * @param userId The user ID whose portfolio to retrieve.
     * @return HTTP response with the portfolio data or an error message.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getPortfolio(@PathVariable String userId) {
        try {
            Portfolio portfolio = portfolioService.getPortfolioByUserId(userId);
            if (portfolio != null) {
                return ResponseEntity.ok(portfolio);
            } else {
                return ResponseEntity.status(404).body("Portfolio not found for user ID: " + userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving portfolio: " + e.getMessage());
        }
    }

    @PostMapping("/similar")
    public List<Document> getSimilarPortfolios(@RequestParam String userId, @RequestParam int topN) {
        return portfolioService.findSimilarPortfolios(userId, topN);
    }
}
