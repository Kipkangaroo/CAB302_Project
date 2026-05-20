package com.lockedin.lockedin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lockedin.lockedin.model.dao.DBExercisesDAO;
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

public class AiWorkoutService {

    private static final String API_URL = "https://integrate.api.nvidia.com/v1/chat/completions";
    private static final String MODEL = "meta/llama-4-maverick-17b-128e-instruct";
    private static final String PROMPT_DIR = "/com/lockedin/lockedin/service/workout_generation_prompt.txt";
    private static final String API_KEY_DIR = "/com/lockedin/lockedin/service/config.file";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final DBExercisesDAO exercisesDAO = new DBExercisesDAO();

    private String getApiKey() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(API_KEY_DIR)) {
            if (input == null)
                throw new IOException("Missing resource file: config.file");
            props.load(input);
        }
        return props.getProperty("nvidia.api.key").trim();
    }

    private String buildPrompt(String experience, String time, String muscleGroup, String goal) throws IOException {
        try (InputStream input = getClass().getResourceAsStream(PROMPT_DIR)) {
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

    public WorkoutResult generateWorkout(String experience, String time, String muscleGroup, String goal) {
        try {
            String key = getApiKey();
            String prompt = buildPrompt(experience, time, muscleGroup, goal);

            List<Map<String, Object>> messages = List.of(
                    Map.of("role", "user", "content", prompt));

            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "messages", messages,
                    "max_tokens", 1024,
                    "temperature", 0.7,
                    "top_p", 1.00,
                    "frequency_penalty", 0.00,
                    "presence_penalty", 0.00);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
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

    public Task<WorkoutResult> createGenerateWorkoutTask(
            String experience, String time, String muscleGroup, String goal) {
        return new Task<>() {
            @Override
            protected WorkoutResult call() {
                return generateWorkout(experience, time, muscleGroup, goal);
            }
        };
    }

    public static class WorkoutResult {
        public final String routineName;
        public final List<WorkoutExerciseEntry> exercises;

        public WorkoutResult(String routineName, List<WorkoutExerciseEntry> exercises) {
            this.routineName = routineName;
            this.exercises = exercises;
        }
    }
}
