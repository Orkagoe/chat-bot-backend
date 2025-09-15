package com.example.chat.service;

import com.example.chat.dto.ChatRequest;
import com.example.chat.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ChatService {

    private final WebClient webClient;

    public ChatService(WebClient.Builder builder,
                       @Value("${openrouter.api.key}") String apiKey) {
        this.webClient = builder
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String askGrok(String message) {
        try {
            ChatRequest request = new ChatRequest(
                    "openai/gpt-4o-mini",
                    List.of(
                            new ChatRequest.Message("system",
                                    "Ты — помощник стриминг-сервиса CineMax. " +
                                            "Отвечай ТОЛЬКО на вопросы о сервисе. " +
                                            "Если вопрос не про CineMax — отвечай: " +
                                            "'Извините, я могу отвечать только на вопросы про CineMax.'"),
                            new ChatRequest.Message("user", message)
                    )
            );

            ChatResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            if (response != null && response.choices != null && !response.choices.isEmpty()) {
                return response.choices.get(0).message.content;
            }
            return "Нет ответа от модели";
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }
}
