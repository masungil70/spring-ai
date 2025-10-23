package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * '생각의 사슬(Chain-of-Thought)' 프롬프트 기법을 사용하여
 * AI의 추론 과정을 단계별로 유도하고 답변을 생성하는 서비스 클래스입니다.
 */
@Service
@Slf4j
public class AiServiceChainOfThoughtPrompt {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 ChatClient 인스턴스

  // ##### 생성자 #####
  public AiServiceChainOfThoughtPrompt(ChatClient.Builder chatClientBuilder) {
    // ChatClient 빌더를 통해 ChatClient 인스턴스를 생성합니다.
    chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####
  /**
   * '생각의 사슬(Chain-of-Thought)' 프롬프트를 사용하여 질문에 대한 답변을 스트림 형태로 반환합니다.
   * "한 걸음씩 생각해 봅시다."라는 문구와 구체적인 예시를 프롬프트에 포함하여,
   * AI가 문제 해결 과정을 단계별로 생각하고 설명하도록 유도합니다.
   *
   * @param question 사용자로부터의 질문
   * @return AI가 생성하는 답변의 스트림 (Flux<String>). AI의 생각 과정을 실시간으로 볼 수 있습니다.
   */
  public Flux<String> chainOfThought(String question) {
    Flux<String> answer = chatClient.prompt()
        .user("""
            %s
            한 걸음씩 생각해 봅시다.
  
            [예시]
            질문: 제 동생이 2살일 때, 저는 그의 나이의 두 배였어요.
            지금 저는 40살인데, 제 동생은 몇 살일까요? 한 걸음씩 생각해 봅시다.
  
            답변: 제 동생이 2살일 때, 저는 2 * 2 = 4살이었어요.
            그때부터 2년 차이가 나며, 제가 더 나이가 많습니다.
            지금 저는 40살이니, 제 동생은 40 - 2 = 38살이에요. 정답은 38살입니다.
            """.formatted(question))
        .stream() // 답변을 스트림 형태로 받습니다.
        .content();
    return answer;
  }
}
