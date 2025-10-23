package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * '자기 일관성(Self-Consistency)' 프롬프트 기법을 구현한 서비스 클래스입니다.
 * 동일한 질문을 여러 번 하고, 가장 많이 나온 답변을 최종 결과로 선택하여
 * AI 응답의 신뢰도를 높이는 방법을 보여줍니다.
 */
@Service
@Slf4j
public class AiServiceSelfConsistency {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 ChatClient 인스턴스

  // 프롬프트 템플릿: 사용자 입력을 받아 AI에게 보낼 최종 프롬프트를 생성합니다.
  private PromptTemplate promptTemplate = PromptTemplate.builder()
      .template("""
          다음 내용을 [IMPORTANT, NOT_IMPORTANT] 둘 중 하나로 분류해 주세요.
          레이블만 반환하세요.
          내용: {content}
          """)
      .build();

  // ##### 생성자 #####
  public AiServiceSelfConsistency(ChatClient.Builder chatClientBuilder) {
    // ChatClient 빌더를 통해 ChatClient 인스턴스를 생성합니다.
    chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####
  /**
   * 자기 일관성 기법을 사용하여 주어진 내용을 분류합니다.
   * AI에게 동일한 질문을 5번 반복하고, 다수결 원칙에 따라 최종 분류를 결정합니다.
   *
   * @param content 사용자가 분류를 요청한 내용
   * @return "중요함" 또는 "중요하지 않음" 이라는 최종 분류 결과
   */
  public String selfConsistency(String content) {
    int importantCount = 0;
    int notImportantCount = 0;
    
    // 프롬프트 템플릿에 사용자 입력을 넣어 완전한 프롬프트를 생성합니다.
    String userText = promptTemplate.render(Map.of("content", content));
  
    // 자기 일관성을 위해 동일한 요청을 5번 반복합니다.
    for (int i = 0; i < 5; i++) {
      // LLM 요청 및 응답 받기
      String output = chatClient.prompt()
          .user(userText)
          .options(ChatOptions.builder()
              // Temperature를 1.0으로 설정하여 응답의 다양성을 높입니다.
              // 온도가 높을수록 AI는 더 창의적이고 무작위적인 답변을 생성하므로,
              // 매번 다른 추론 경로를 탐색하게 되어 자기 일관성 기법에 적합합니다.
              .temperature(1.0)
              .build())
          .call()
          .content();
  
      log.info("시도 {}: {}", i + 1, output);
  
      // 각 시도의 결과를 집계합니다.
      if (output.contains("IMPORTANT")) {
        importantCount++;
      } else {
        notImportantCount++;
      }
    }
  
    // 다수결로 최종 분류를 결정합니다.
    String finalClassification = importantCount > notImportantCount ?
            "중요함" : "중요하지 않음";
    
    log.info("최종 결과: {}, (중요: {}표, 중요하지 않음: {}표)", 
        finalClassification, importantCount, notImportantCount);

    return finalClassification;
  }
}
