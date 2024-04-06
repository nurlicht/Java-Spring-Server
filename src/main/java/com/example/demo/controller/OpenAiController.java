package com.example.demo.controller;

import java.util.List;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api/ai/")
public class OpenAiController {

    private final OpenAiChatClient openAiChatClient;

    @PostMapping("chat")
    public ChatResponse post(@RequestBody final String question) {
        log.info("question={}.", question);
        final Prompt prompt = new Prompt(new AssistantMessage(question));
        log.info("prompt={}.", prompt);
        ChatResponse chatResponse;
        try {
            chatResponse = openAiChatClient.stream(prompt).blockFirst();
        } catch (final WebClientResponseException e) {
            chatResponse = new ChatResponse(List.of(new Generation(e.getMessage())));
        }
        log.info("chatResponse={}.", chatResponse);
        return chatResponse;
    }

}
