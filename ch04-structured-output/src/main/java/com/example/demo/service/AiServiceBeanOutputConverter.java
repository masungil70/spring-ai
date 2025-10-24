package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Hotel;

import lombok.extern.slf4j.Slf4j;

/**
 * AI의 응답을 특정 자바 객체(POJO)로 변환하는 BeanOutputConverter의 사용법을 보여주는 서비스 클래스입니다.
 * 동일한 목표를 고수준(High-Level) API와 저수준(Low-Level) API 두 가지 방식으로 구현하여 비교합니다.
 */
@Service
@Slf4j
public class AiServiceBeanOutputConverter {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 클라이언트

  // ##### 생성자 #####
  public AiServiceBeanOutputConverter(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####

  /**
   * 저수준(Low-Level) API를 사용하여 AI의 응답을 'Hotel' 객체로 변환합니다.
   * 개발자가 변환의 모든 단계를 직접 제어하며, 디버깅이나 복잡한 프롬프트 구성에 유리합니다.
   *
   * @param city 도시 이름
   * @return AI가 생성한 정보를 담은 Hotel 객체
   */
  public Hotel beanOutputConverterLowLevel(String city) {
    // 1. BeanOutputConverter를 생성하며, 변환할 대상 클래스(Hotel.class)를 명시합니다.
    BeanOutputConverter<Hotel> beanOutputConverter = new BeanOutputConverter<>(Hotel.class);

    // 2. 프롬프트 템플릿에 출력 형식을 지정하는 {format} 플레이스홀더를 직접 포함시킵니다.
    PromptTemplate promptTemplate = PromptTemplate.builder()
        .template("{city}에서 유명한 호텔 목록 5개를 추천해 주세요. {format}")
//        .template("{city}에서 유명한 호텔 한 곳을 추천해 주세요. {format}")
        .build();

    // 3. converter.getFormat()을 호출하여 Hotel 클래스 구조에 맞는 JSON 스키마 정보를 가져와 프롬프트를 완성합니다.
    Prompt prompt = promptTemplate.create(Map.of(
        "city", city,
        "format", beanOutputConverter.getFormat()));

    // 4. AI를 호출하여 순수한 JSON 텍스트 응답을 받습니다.
    String json = chatClient.prompt(prompt)
        .call()
        .content();
    log.info("AI 원본 응답 (JSON): {}", json);

    // 5. 변환기의 convert() 메소드를 직접 호출하여, 응답 JSON을 Hotel 객체로 변환합니다.
    Hotel hotel = beanOutputConverter.convert(json);
    return hotel;
  }
  
  /**
   * 고수준(High-Level) API를 사용하여 AI의 응답을 'Hotel' 객체로 변환합니다.
   * 코드가 매우 간결하며, 자바 객체로 변환하는 대부분의 경우에 권장되는 방식입니다.
   *
   * @param city 도시 이름
   * @return AI가 생성한 정보를 담은 Hotel 객체
   */
  public Hotel beanOutputConverterHighLevel(String city) {
    // .entity() 메소드에 변환할 클래스(Hotel.class)를 직접 전달합니다.
    // 이렇게 하면 Spring AI가 내부적으로 BeanOutputConverter<Hotel>를 생성하고,
    // 저수준 API의 1~5번 과정을 모두 자동으로 처리해 줍니다.
    Hotel hotel = chatClient.prompt()
        .user("%s에서 유명한 호텔 목록 5개를 추천해 주세요.".formatted(city))
        //.user("%s에서 유명한 호텔 한 곳을 추천해 주세요.".formatted(city))
        .call()
        .entity(Hotel.class);
    return hotel;
  }
}
