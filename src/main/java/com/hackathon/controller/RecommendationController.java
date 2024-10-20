package com.hackathon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hackathon.service.RecommendationService;

@RestController
@RequestMapping("/recommendations")  // Base URL for recommendation-related endpoints
public class RecommendationController {

    private final RecommendationService recommendationService;

    // Constructor-based dependency injection
    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // Expose the investment recommendation API
    @GetMapping("/{userId}")
    public String getRecommendations(@PathVariable String userId) {
        // Call the MLModelService to provide recommendations for the given userId
        return recommendationService.provideInvestmentRecommendations(userId);
    }
}

