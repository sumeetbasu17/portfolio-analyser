package com.hackathon.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import com.mongodb.client.model.Aggregates;
import java.util.Arrays;
import java.util.List;

@Service
public class VectorSearchService {
    private MongoCollection<Document> portfolioCollection;

    public VectorSearchService(MongoDatabase database) {
        portfolioCollection = database.getCollection("portfolios");
    }

    public List<Document> searchSimilarPortfolios(List<Double> targetVector, int topN) {
        // MongoDB Atlas Vector Search aggregation pipeline
        Bson vectorSearchStage = new Document("$search",
                new Document("knnBeta", new Document("vector", targetVector)
                        .append("path", "vector")
                        .append("k", topN)
                        .append("similarity", new Document("cosine", new Document()))));

        List<Bson> pipeline = Arrays.asList(vectorSearchStage,
                Aggregates.limit(topN));

        return portfolioCollection.aggregate(pipeline).into(new java.util.ArrayList<>());
    }
}
