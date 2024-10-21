package com.hackathon.client;

import okhttp3.*;
import com.google.gson.*;
import com.hackathon.dto.Trade;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AlpacaClient {
    private static final String BASE_URL = "https://paper-api.alpaca.markets/v2";
    private static final String DATA_URL = "https://data.alpaca.markets/v2";
    private static final String POSITIONS_ENDPOINT = "/v2/positions";
    private static final String API_KEY = "PK7CCLTO7LPDW0PIQ20H";
    private static final String API_SECRET = "Aun2P7vqMauzIbHkexfdyIn1hjB3LL2E9qVr8Nu3";

    private OkHttpClient httpClient;

    public AlpacaClient() {
        this.httpClient = new OkHttpClient();
    }

    public List<Trade> getPositions() throws IOException {
        String url = BASE_URL + POSITIONS_ENDPOINT;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("APCA-API-KEY-ID", API_KEY)
                .addHeader("APCA-API-SECRET-KEY", API_SECRET)
                .build();

        Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        JsonArray positionsArray = JsonParser.parseString(responseBody).getAsJsonArray();

        List<Trade> trades = new ArrayList<>();
        for (JsonElement element : positionsArray) {
            JsonObject position = element.getAsJsonObject();

            String symbol = position.get("symbol").getAsString();
            int quantity = position.get("qty").getAsInt();
            double price = position.get("avg_entry_price").getAsDouble();
            // For demo purposes, using current date as trade date
            LocalDate tradeDate = LocalDate.now();
            String action = "buy"; // Positions are open positions, assuming "buy"

            trades.add(new Trade(symbol, quantity, tradeDate, action, price, null));
        }

        return trades;
    }

    // Fetch current price for a symbol
    public double getCurrentPrice(String symbol) throws IOException {
        String url = DATA_URL + "/stocks/" + symbol + "/quotes/latest";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("APCA-API-KEY-ID", API_KEY)
                .addHeader("APCA-API-SECRET-KEY", API_SECRET)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            double price = jsonObject.getAsJsonObject("quote").get("askprice").getAsDouble();
            return price;
        }
    }

    // Fetch historical price for a symbol at a specific date range (e.g., 1Y, 1W)
    public double getHistoricalPrice(String symbol, String timeRange) throws IOException {
        String startTime = getTimeForRange(timeRange); // Implement logic for calculating the start time
        String endTime = "now";  // Fetch until the latest data

        String url = DATA_URL + "/stocks/" + symbol + "/bars?start=" + startTime + "&end=" + endTime + "&timeframe=1Day";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("APCA-API-KEY-ID", API_KEY)
                .addHeader("APCA-API-SECRET-KEY", API_SECRET)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray bars = jsonObject.getAsJsonArray("bars");

            // Return the first bar (earliest in the time range)
            if (bars.size() > 0) {
                JsonObject firstBar = bars.get(0).getAsJsonObject();
                return firstBar.get("c").getAsDouble();  // 'c' is the closing price
            } else {
                throw new IOException("No historical data found for symbol " + symbol);
            }
        }
    }

    // Fetch historical prices for a symbol over a period (e.g., 1M, 1Y)
    public List<Double> getHistoricalPrices(String symbol, String timeRange) throws IOException {
        String startTime = getTimeForRange(timeRange); // Implement logic for calculating the start time
        String endTime = "now";  // Fetch until the latest data

        String url = DATA_URL + "/stocks/" + symbol + "/bars?start=" + startTime + "&end=" + endTime + "&timeframe=1Day";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("APCA-API-KEY-ID", API_KEY)
                .addHeader("APCA-API-SECRET-KEY", API_SECRET)
                .build();

        List<Double> prices = new ArrayList<>();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray bars = jsonObject.getAsJsonArray("bars");

            // Collect all the closing prices in the time range
            for (int i = 0; i < bars.size(); i++) {
                JsonObject bar = bars.get(i).getAsJsonObject();
                prices.add(bar.get("c").getAsDouble());  // 'c' is the closing price
            }
        }

        return prices;
    }

    private String getTimeForRange(String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;

        // Check the time range and subtract the corresponding amount of time
        switch (timeRange) {
            case "1Y":
                startTime = now.minusYears(1);
                break;
            case "6M":
                startTime = now.minusMonths(6);
                break;
            case "3M":
                startTime = now.minusMonths(3);
                break;
            case "1M":
                startTime = now.minusMonths(1);
                break;
            case "1W":
                startTime = now.minusWeeks(1);
                break;
            case "1D":
                startTime = now.minusDays(1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time range: " + timeRange);
        }

        // Convert the start time to ISO-8601 format with a UTC timezone ('Z' at the end)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return startTime.atZone(ZoneOffset.UTC).format(formatter);
    }
}
