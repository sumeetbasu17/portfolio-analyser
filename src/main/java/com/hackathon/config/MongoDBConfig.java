package com.hackathon.config;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

@Configuration
public class MongoDBConfig {
    private static final String CONNECTION_STRING = "mongodb+srv://sumeetbasu17:dwpdtK67nOUcqMvx@portfolioanalysercluste.r5fx6.mongodb.net/?retryWrites=true&w=majority&appName=PortfolioAnalyserCluster";
    private static final String DATABASE_NAME = "portfolioDB";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(CONNECTION_STRING);
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    @Bean
    public MongoCollection<Document> portfolioCollection(MongoDatabase mongoDatabase) {
        return mongoDatabase.getCollection("portfolio");
    }
    
}
