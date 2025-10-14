package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiServiceDefaultMethod {
  // ##### 필드 #####
  private ChatClient chatClient;

  // ##### 생성자 #####
  public AiServiceDefaultMethod(ChatClient.Builder chatClientBuilder) {
    chatClient = chatClientBuilder
        .defaultSystem("""
          적절한 감탄사, 웃음등을 넣어서 친절하게 대화를 해주세요.
          모든 답변은 반드시 한국어로 해야 합니다.
          """)
        .defaultOptions(ChatOptions.builder()
            .temperature(1.0)
            .maxTokens(300)
            .model("gpt-3.5-turbo")
            .build())
        .build();
  }

  // ##### 메소드 #####
  public Flux<String> defaultMethod(String question) {   
    Flux<String> response = chatClient.prompt()
        .user(question)
        .stream()
        .content();
    return response;
  }
}
