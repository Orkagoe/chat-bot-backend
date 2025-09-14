package com.example.chat.controller;

import com.example.chat.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String ask(@RequestBody String message) {
        return chatService.askGrok(message);
    }

}
