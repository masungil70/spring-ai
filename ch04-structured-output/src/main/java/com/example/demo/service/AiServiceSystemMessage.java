package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ReviewClassification;

import lombok.extern.slf4j.Slf4j;

/**
 * System 메시지를 사용하여 AI의 역할이나 행동 지침을 설정하는 방법을 보여주는 서비스 클래스입니다.
 * AI에게 "너는 이제부터 리뷰 분석 전문가야" 와 같이 역할을 부여하여 더 일관되고 정확한 응답을 유도합니다.
 */
@Service
@Slf4j
public class AiServiceSystemMessage {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 클라이언트

  // ##### 생성자 #####
  public AiServiceSystemMessage(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####
  /**
   * System 메시지를 활용하여 주어진 영화 리뷰를 [긍정, 중립, 부정]으로 분류합니다.
   *
   * @param review 사용자가 입력한 영화 리뷰 텍스트
   * @return 분류 결과와 추가 정보를 담은 ReviewClassification 객체
   */
  public ReviewClassification classifyReview(String review) {
    ReviewClassification reviewClassification = chatClient.prompt()
        // 1. System 메시지 설정: AI에게 전체 대화의 맥락이나 역할을 부여합니다.
        //    여기서는 "리뷰를 분류하고 JSON으로 반환하라"는 기본 지침을 설정하여,
        //    AI가 '리뷰 분류기'로서 행동하도록 만듭니다.
        .system("""
            영화 리뷰를 [POSITIVE, NEUTRAL, NEGATIVE] 중에서 하나로 분류하고,
            유효한 JSON을 반환하세요.
         """)
        // 2. User 메시지 설정: AI가 처리해야 할 실제 데이터(사용자 입력)를 전달합니다.
        .user("%s".formatted(review))
        // 3. 옵션 설정: AI의 응답 방식을 제어합니다.
        //    - temperature를 0.0으로 설정: AI의 응답에서 무작위성을 제거하여, 동일한 입력에 대해 항상 동일한 결과를 내도록 합니다.
        //      분류(Classification)와 같이 일관성이 중요한 작업에 필수적인 옵션입니다.
        .options(ChatOptions.builder().temperature(0.0).build())
        // 4. AI 호출 및 고수준 출력 변환
        //    .entity()를 사용하여 AI의 JSON 응답을 ReviewClassification 객체로 자동 변환합니다.
        .call()
        .entity(ReviewClassification.class);
    return reviewClassification;
  }
}


