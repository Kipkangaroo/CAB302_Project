package com.lockedin.lockedin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.entity.diet.Food;

import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service for AI model operations.
 * @author LockedIn Team
 * @version 1.0
 */
public class AIModelService {
    private final int userId;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

        /**
     * Constructs a AIModelService using default application dependencies.
     * @param userId The user id.
     */
    public AIModelService(int userId) {
        this.userId = userId;
    }

            /**
     * Returns the api key.
     * @return api key
     * @throws IOException If the operation fails.
     */
    private String getApiKey() throws IOException {
        final String apiKeyDir = "/com/lockedin/lockedin/service/config.file";
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(apiKeyDir)) {
            if (input == null) {
                throw new IOException("Missing resource file: config.file");
            }
            props.load(input);
        }
        return props.getProperty("nvidia.api.key").trim();
    }

        /**
     * Analyze image with prompt.
     * @param imagePath The image path.
     * @param date date
     */
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
                food.setUserId(this.userId);
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

        /**
     * Send image with prompt.
     * @param imagePath The image path.
     * @param date date
     */
    public Food sendImageWithPrompt(Path imagePath, LocalDate date) {
        Food food = analyzeImageWithPrompt(imagePath, date);
        if (food != null) {
            new FoodDAO().addFood(food, date);
        }
        return food;
    }

        /**
     * Create analyze image task.
     * @param imagePath The image path.
     * @param date date
     * @return A JavaFX task that performs the work on a background thread.
     */
    public Task<Food> createAnalyzeImageTask(Path imagePath, LocalDate date) {
        return new Task<>() {
            /**
             * Runs the background task and returns its result.
             */
            @Override
            protected Food call() {
                return analyzeImageWithPrompt(imagePath, date);
            }
        };
    }

        /**
     * Build body.
     * @param messages messages
     * @return resulting text
     */
    private Map<String, Object> buildBody(List<Map<String, Object>> messages) {
        final String model = "meta/llama-4-maverick-17b-128e-instruct";
        return Map.of(
                "model", model,
                "messages", messages,
                "max_tokens", 512,
                "temperature", 1.00,
                "top_p", 1.00,
                "frequency_penalty", 0.00,
                "presence_penalty", 0.00);
    }

        /**
     * Build request.
     * @param key key
     * @param body body
     */
    private HttpRequest buildRequest(String key, Map<String, Object> body) {
        final String apiUrl = "https://integrate.api.nvidia.com/v1/chat/completions";
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
    }

        /**
     * Post and parse.
     * @param key key
     * @param body body
     * @return resulting text
     */
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

        /**
     * Build data uri.
     * @param imagePath The image path.
     * @return resulting text
     * @throws Exception If the operation fails.
     */
    private String buildDataUri(Path imagePath) throws Exception {
        byte[] bytes = Files.readAllBytes(imagePath);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String mediaType = imagePath.getFileName().toString().toLowerCase().endsWith(".png")
                ? "image/png"
                : "image/jpeg";
        return "data:" + mediaType + ";base64," + base64;
    }

        /**
     * Read food image prompt.
     * @return resulting text
     * @throws IOException If the operation fails.
     */
    private String readFoodImagePrompt() throws IOException {
        final String foodImagePromptDir = "/com/lockedin/lockedin/service/food_image_prompt.txt";
        try (InputStream input = getClass().getResourceAsStream(foodImagePromptDir)) {
            if (input == null) {
                throw new IOException("Missing resource file: " + foodImagePromptDir);
            }
            return new String(input.readAllBytes());
        }
    }
}
