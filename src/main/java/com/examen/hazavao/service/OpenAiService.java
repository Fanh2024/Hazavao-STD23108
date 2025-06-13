package com.examen.hazavao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class OpenAiService {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @SneakyThrows
    public String getDefinition(String word) {
        String prompt = "Hazavao amin'ny teny malagasy fohy ilay teny: \"" + word + "\"";

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        ));

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("OpenAI API error: " + response.body().string());
        }

        Map<?, ?> result = objectMapper.readValue(response.body().string(), Map.class);
        var message = ((Map<?, ?>) ((Map<?, ?>) ((java.util.List<?>) result.get("choices")).get(0)).get("message")).get("content");

        return message.toString().trim();
    }
}
