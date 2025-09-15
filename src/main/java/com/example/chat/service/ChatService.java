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
                                    "Ты — умный и вежливый помощник стриминг-сервиса CineMax. " +
                                            "Отвечай подробно и полезно на все вопросы о CineMax: фильмы, подписка, приложение, сайт, функционал. " +
                                            "Если пользователь задаёт вопрос, который не имеет отношения к CineMax, " +
                                            "не отвечай на него напрямую, а мягко откажи, сказав: " +
                                            "'Извините, я могу помогать только с вопросами про CineMax.'")
                            ,
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
