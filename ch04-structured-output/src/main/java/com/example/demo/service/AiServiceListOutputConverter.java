package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * AI의 응답을 List<String> 형태로 변환하는 ListOutputConverter의 사용법을 보여주는 서비스 클래스입니다.
 * 동일한 목표를 고수준(High-Level) API와 저수준(Low-Level) API 두 가지 방식으로 구현하여 차이점을 비교합니다.
 */
@Service
@Slf4j
public class AiServiceListOutputConverter {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 클라이언트

  // ##### 생성자 #####
  public AiServiceListOutputConverter(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####

  /**
   * 저수준(Low-Level) API를 사용하여 AI의 응답을 List<String>으로 변환합니다.
   * 개발자가 변환의 모든 단계를 직접 제어하며, 디버깅이나 복잡한 프롬프트 구성에 유리합니다.
   *
   * @param city 도시 이름
   * @return 해당 도시의 호텔 목록
   */
  public List<String> listOutputConverterLowLevel(String city) {
    // 1. ListOutputConverter를 생성합니다. AI 응답(쉼표로 구분된 문자열)을 List<String>으로 변환하는 역할을 합니다.
    ListOutputConverter converter = new ListOutputConverter();

    // 2. 프롬프트 템플릿에 출력 형식을 지정하는 {format} 플레이스홀더를 직접 포함시킵니다.
    PromptTemplate promptTemplate = PromptTemplate.builder()
        .template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
        .build();

    // 3. converter.getFormat()을 호출하여 AI에게 전달할 형식 정보를 가져와, 프롬프트를 최종 완성합니다.
    Prompt prompt = promptTemplate.create(
        Map.of("city", city, "format", converter.getFormat()));

    // 4. AI를 호출하여 순수한 텍스트(쉼표로 구분된 문자열) 응답을 받습니다.
    String commaSeparatedString = chatClient.prompt(prompt)
        .call()
        .content();
    log.info("AI 원본 응답: {}", commaSeparatedString);

    // 5. 변환기의 convert() 메소드를 직접 호출하여, 응답 문자열을 List<String>으로 변환합니다.
    List<String> hotelList = converter.convert(commaSeparatedString);
    return hotelList;
  }
  
  /**
   * 고수준(High-Level) API를 사용하여 AI의 응답을 List<String>으로 변환합니다.
   * 코드가 매우 간결하며, 대부분의 일반적인 경우에 권장되는 방식입니다.
   *
   * @param city 도시 이름
   * @return 해당 도시의 호텔 목록
   */
  public List<String> listOutputConverterHighLevel(String city) {
    // .entity() 메소드에 ListOutputConverter 인스턴스를 전달하는 것만으로,
    // 저수준 API의 1~5번 과정(프롬프트 보강, AI 호출, 응답 파싱)이 모두 자동으로 처리됩니다.
    List<String> hotelList = chatClient.prompt()
        .user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
        .call()
        .entity(new ListOutputConverter());
    return hotelList;
  }
}
