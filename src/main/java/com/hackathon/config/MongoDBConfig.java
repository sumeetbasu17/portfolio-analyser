package com.hackathon.config;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBConfig {
    private static final String CONNECTION_STRING = "mongodb+srv://sumeetbasu17:root@portfolioanalysercluste.r5fx6.mongodb.net/?retryWrites=true&w=majority&appName=PortfolioAnalyserCluster";
    private static final String DATABASE_NAME = "portfolioDB";

    public static MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}
