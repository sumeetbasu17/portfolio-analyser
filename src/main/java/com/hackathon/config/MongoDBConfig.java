package com.hackathon.config;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBConfig {
    private static final String CONNECTION_STRING = "mongodb+srv://<username>:<password>@cluster0.mongodb.net/portfolioDB?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "portfolioDB";

    public static MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}
