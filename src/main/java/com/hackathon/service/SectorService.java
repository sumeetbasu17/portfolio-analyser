package com.hackathon.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class SectorService {

    private MongoCollection<Document> portfolioCollection;
    private static final String IEX_API_TOKEN = "<your_api_token>";
    private static final String IEX_API_URL = "https://cloud.iexapis.com/stable/stock/%s/company?token=" + IEX_API_TOKEN;

    public SectorService(MongoDatabase database) {
        this.portfolioCollection = database.getCollection("portfolios");
    }

    /**
     * Fetch distinct sectors from MongoDB.
     * 
     * @return A list of distinct sectors in the database.
     */
    public List<String> getDistinctSectors() {
        // Get distinct sectors from the "trades.sector" field in the MongoDB collection
        return portfolioCollection.distinct("trades.sector", String.class).into(new ArrayList<>());
    }

    /**
     * Fetch sector information for a stock symbol from IEX Cloud or a similar API.
     * 
     * @param symbol The stock symbol (e.g., AAPL, MSFT) for which to fetch sector information.
     * @return The sector of the stock.
     * @throws IOException If an error occurs while calling the API.
     */
    public String fetchSectorFromAPI(String symbol) throws IOException {
        // Construct API URL
        String apiUrl = String.format(IEX_API_URL, symbol);

        // Make an HTTP request to fetch data
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        // Read response from API
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // Parse JSON response to extract the sector field (this is a simplified example)
        String jsonResponse = response.toString();
        String sector = parseSectorFromResponse(jsonResponse);

        return sector;
    }

    /**
     * Helper method to parse the sector from the API JSON response.
     * 
     * @param jsonResponse The JSON response string from the API.
     * @return The sector extracted from the response.
     */
    private String parseSectorFromResponse(String jsonResponse) {
        // In a real-world application, use a JSON parsing library like Jackson or Gson
        // This is a simple string matching for illustration purposes
        String sectorKey = "\"sector\":\"";
        int startIndex = jsonResponse.indexOf(sectorKey) + sectorKey.length();
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }
}
