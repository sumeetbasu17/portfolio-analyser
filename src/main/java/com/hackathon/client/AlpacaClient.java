package com.hackathon.client;

import okhttp3.*;
import com.google.gson.*;
import com.hackathon.dto.Trade;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlpacaClient {
    private static final String API_BASE_URL = "https://paper-api.alpaca.markets";
    private static final String POSITIONS_ENDPOINT = "/v2/positions";
    private static final String API_KEY = "<Your_Alpaca_API_Key>";
    private static final String API_SECRET = "<Your_Alpaca_API_Secret>";

    private OkHttpClient httpClient;
    private Gson gson;

    public AlpacaClient() {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    public List<Trade> getPositions() throws IOException {
        String url = API_BASE_URL + POSITIONS_ENDPOINT;
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
}
