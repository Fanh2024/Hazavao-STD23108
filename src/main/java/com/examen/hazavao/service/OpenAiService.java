package com.examen.hazavao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;

    // 🔑 Injection sécurisée via @Value
    public OpenAiService(@Value("${openai.api.key}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Missing OpenAI API key. Did you set OPENAI_API_KEY env var?");
        }
        this.apiKey = apiKey;
    }

    @SneakyThrows
    public String getDefinition(String word) {
        String prompt = "Hazavao amin'ny teny malagasy fohy ilay teny: \"" + word + "\"";

        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        String requestBody = objectMapper.writeValueAsString(body);

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI API error: " + response.code() + " - " + response.body().string());
            }

            Map<?, ?> result = objectMapper.readValue(response.body().string(), Map.class);
            List<?> choices = (List<?>) result.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "Tsy misy valiny avy amin'ny OpenAI.";
            }

            Map<?, ?> messageMap = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
            return messageMap.get("content").toString().trim();
        }
    }
}
