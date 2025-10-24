package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * AI의 응답을 Map<String, Object> 형태로 변환하는 MapOutputConverter의 사용법을 보여주는 서비스 클래스입니다.
 * 이 변환기는 미리 정해진 DTO(Java Bean) 없이, 유연한 키-값 형태의 데이터를 받아야 할 때 유용합니다.
 */
@Service
@Slf4j
public class AiServiceMapOutputConverter {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 클라이언트

  // ##### 생성자 #####
  public AiServiceMapOutputConverter(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####

  /**
   * 저수준(Low-Level) API를 사용하여 AI의 응답을 Map<String, Object>으로 변환합니다.
   * 개발자가 변환의 모든 단계를 직접 제어하며, 디버깅이나 복잡한 프롬프트 구성에 유리합니다.
   *
   * @param hotel 호텔 이름
   * @return 해당 호텔의 정보를 담은 Map 객체
   */
  public Map<String, Object> mapOutputConverterLowLevel(String hotel) {
    // 1. MapOutputConverter를 생성합니다.
    MapOutputConverter mapOutputConverter = new MapOutputConverter();

    // 2. 프롬프트 템플릿에 출력 형식을 지정하는 {format} 플레이스홀더를 직접 포함시킵니다.
    PromptTemplate promptTemplate = new PromptTemplate(
        "호텔 {hotel}에 대해 정보를 알려주세요 {format}");

    // 3. converter.getFormat()을 호출하여 AI에게 JSON 객체로 응답하라는 지시사항을 가져와 프롬프트를 완성합니다.
    Prompt prompt = promptTemplate.create(Map.of(
        "hotel", hotel,
        "format", mapOutputConverter.getFormat()));

    // 4. AI를 호출하여 순수한 JSON 텍스트 응답을 받습니다.
    String json = chatClient.prompt(prompt)
        .call()
        .content();
    log.info("AI 원본 응답 (JSON): {}", json);

    // 5. 변환기의 convert() 메소드를 직접 호출하여, 응답 JSON을 Map<String, Object> 객체로 변환합니다.
    Map<String, Object> hotelInfo = mapOutputConverter.convert(json);
    return hotelInfo;
  }
  
  /**
   * 고수준(High-Level) API를 사용하여 AI의 응답을 Map<String, Object>으로 변환합니다.
   * 코드가 매우 간결하며, 대부분의 일반적인 경우에 권장되는 방식입니다.
   *
   * @param hotel 호텔 이름
   * @return 해당 호텔의 정보를 담은 Map 객체
   */
  public Map<String, Object> mapOutputConverterHighLevel(String hotel) {
    // .entity() 메소드에 MapOutputConverter 인스턴스를 전달하는 것만으로,
    // 저수준 API의 모든 과정(프롬프트 보강, AI 호출, 응답 파싱)이 자동으로 처리됩니다.
    Map<String, Object> hotelInfo = chatClient.prompt()
        .user("호텔 %s에 대해 정보를 알려주세요".formatted(hotel))
        .call()
        .entity(new MapOutputConverter());
    return hotelInfo;
  }

}
