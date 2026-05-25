package com.lockedin.lockedin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lockedin.lockedin.model.dao.ExercisesDAO;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service for AI workout generation operations.
 * @author LockedIn Team
 * @version 1.0
 */
public class AIWorkoutService {

            /**
     * Returns the api key.
     * @return api key
     * @throws IOException If the operation fails.
     */
    private String getApiKey() throws IOException {
        final String apiKeyDir = "/com/lockedin/lockedin/service/config.file";
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(apiKeyDir)) {
            if (input == null)
                throw new IOException("Missing resource file: config.file");
            props.load(input);
        }
        return props.getProperty("nvidia.api.key").trim();
    }

        /**
     * Build prompt.
     * @param experience experience
     * @param time time
     * @param muscleGroup The muscle group.
     * @param goal goal
     * @return resulting text
     * @throws IOException If the operation fails.
     */
    private String buildPrompt(String experience, String time, String muscleGroup, String goal) throws IOException {
        final String promptDir = "/com/lockedin/lockedin/service/workout_generation_prompt.txt";
        try (InputStream input = getClass().getResourceAsStream(promptDir)) {
            if (input == null)
                throw new IOException("Missing resource file: workout_generation_prompt.txt");
            String template = new String(input.readAllBytes());
            return template
                    .replace("{experience}", experience)
                    .replace("{time}", time)
                    .replace("{muscleGroup}", muscleGroup)
                    .replace("{goal}", goal);
        }
    }

        /**
     * Generate workout.
     * @param experience experience
     * @param time time
     * @param muscleGroup The muscle group.
     * @param goal goal
     */
    public WorkoutResult generateWorkout(String experience, String time, String muscleGroup, String goal) {
        final String apiUrl = "https://integrate.api.nvidia.com/v1/chat/completions";
        final String model = "meta/llama-4-maverick-17b-128e-instruct";
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        ExercisesDAO exercisesDAO = new ExercisesDAO();
        try {
            String key = getApiKey();
            String prompt = buildPrompt(experience, time, muscleGroup, goal);

            List<Map<String, Object>> messages = List.of(
                    Map.of("role", "user", "content", prompt));

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", messages,
                    "max_tokens", 1024,
                    "temperature", 0.7,
                    "top_p", 1.00,
                    "frequency_penalty", 0.00,
                    "presence_penalty", 0.00);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + key)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("API error " + response.statusCode() + ": " + response.body());
                return null;
            }

            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
            String content = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString().trim();

            // Strip markdown code fences if the model wrapped its output
            if (content.startsWith("```")) {
                int start = content.indexOf('\n') + 1;
                int end = content.lastIndexOf("```");
                if (end > start)
                    content = content.substring(start, end).trim();
            }

            JsonObject result = gson.fromJson(content, JsonObject.class);
            String routineName = result.get("routine_name").getAsString();
            JsonArray exercisesArray = result.getAsJsonArray("exercises");

            List<WorkoutExerciseEntry> entries = new ArrayList<>();
            for (int i = 0; i < exercisesArray.size(); i++) {
                JsonObject ex = exercisesArray.get(i).getAsJsonObject();
                String name = ex.get("name").getAsString();
                int sets = ex.get("sets").getAsInt();
                int reps = ex.get("reps").getAsInt();
                int rest = ex.get("rest_seconds").getAsInt();

                int exerciseId = exercisesDAO.findExerciseIdByName(name);
                entries.add(new WorkoutExerciseEntry(0, exerciseId, name, sets, reps, rest));
            }

            return new WorkoutResult(routineName, entries);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        /**
     * Create generate workout task.
     * @param experience experience
     * @param time time
     * @param muscleGroup The muscle group.
     * @param goal goal
     * @return A JavaFX task that performs the work on a background thread.
     */
    public Task<WorkoutResult> createGenerateWorkoutTask(
            String experience, String time, String muscleGroup, String goal) {
        return new Task<>() {
            /**
             * Runs the background task and returns its result.
             */
            @Override
            protected WorkoutResult call() {
                return generateWorkout(experience, time, muscleGroup, goal);
            }
        };
    }

    /**
     * Provides workout result functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    public static class WorkoutResult {
        public final String routineName;
        public final List<WorkoutExerciseEntry> exercises;

            /**
     * Constructs a WorkoutResult using default application dependencies.
     * @param routineName The routine name.
     * @param exercises exercises
     */
        public WorkoutResult(String routineName, List<WorkoutExerciseEntry> exercises) {
            this.routineName = routineName;
            this.exercises = exercises;
        }
    }
}
