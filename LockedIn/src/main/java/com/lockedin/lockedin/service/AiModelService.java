package com.lockedin.lockedin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javafx.concurrent.Task;
import java.time.LocalDate;

import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.entity.Food;

public class AiModelService {
    private final int userID;
    private static final String API_URL = "https://integrate.api.nvidia.com/v1/chat/completions";
    private static final String MODEL = "meta/llama-4-maverick-17b-128e-instruct";
    private static final String FOOD_IMAGE_PROMPT_DIR = "/com/lockedin/lockedin/service/food_image_prompt.txt";
    private static final String API_KEY_DIR = "/com/lockedin/lockedin/service/config.file";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final FoodDAO foodDAO = new FoodDAO();

    public AiModelService(int userID) {
        this.userID = userID;
    }

    private String getApiKey() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(API_KEY_DIR)) {
            if (input == null) {
                throw new IOException("Missing resource file: config.file");
            }
            props.load(input);
        }
        return props.getProperty("nvidia.api.key").trim();
    }

    public Food analyzeImageWithPrompt(Path imagePath, LocalDate date) {
        try {
            String key = getApiKey();
            List<Map<String, Object>> content = List.of(
                    Map.of("type", "image_url", "image_url", Map.of("url", buildDataUri(imagePath))),
                    Map.of("type", "text", "text", this.readFoodImagePrompt()));
            String output = postAndParse(key, buildBody(
                    List.of(Map.of("role", "user", "content", content))));
            if (output != null) {
                JsonObject jsonOutput = gson.fromJson(output, JsonObject.class);
                Food food = new Food();
                food.setId(0);
                food.setUserID(this.userID);
                food.setName(jsonOutput.get("name").getAsString());
                food.setProtein(jsonOutput.get("protein").getAsInt());
                food.setCarbs(jsonOutput.get("carb").getAsInt());
                food.setFats(jsonOutput.get("fat").getAsInt());
                food.setCalories(jsonOutput.get("calories").getAsInt());
                food.setDate(date);
                return food;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Food sendImageWithPrompt(Path imagePath, LocalDate date) {
        Food food = analyzeImageWithPrompt(imagePath, date);
        if (food != null) {
            foodDAO.addFood(food, date);
        }
        return food;
    }

    public Task<Food> createAnalyzeImageTask(Path imagePath, LocalDate date) {
        return new Task<>() {
            @Override
            protected Food call() {
                return analyzeImageWithPrompt(imagePath, date);
            }
        };
    }

    private Map<String, Object> buildBody(List<Map<String, Object>> messages) {
        return Map.of(
                "model", MODEL,
                "messages", messages,
                "max_tokens", 512,
                "temperature", 1.00,
                "top_p", 1.00,
                "frequency_penalty", 0.00,
                "presence_penalty", 0.00);
    }

    private HttpRequest buildRequest(String key, Map<String, Object> body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
    }

    private String postAndParse(String key, Map<String, Object> body) {
        try {
            HttpResponse<String> response = client.send(
                    buildRequest(key, body), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("API error " + response.statusCode() + ": " + response.body());
                return null;
            }
            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            JsonArray choices = json.getAsJsonArray("choices");
            return choices.get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildDataUri(Path imagePath) throws Exception {
        byte[] bytes = Files.readAllBytes(imagePath);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String mediaType = imagePath.getFileName().toString().toLowerCase().endsWith(".png")
                ? "image/png"
                : "image/jpeg";
        return "data:" + mediaType + ";base64," + base64;
    }

    private String readFoodImagePrompt() throws IOException {
        try (InputStream input = getClass().getResourceAsStream(FOOD_IMAGE_PROMPT_DIR)) {
            if (input == null) {
                throw new IOException("Missing resource file: " + FOOD_IMAGE_PROMPT_DIR);
            }
            return new String(input.readAllBytes());
        }
    }
}
