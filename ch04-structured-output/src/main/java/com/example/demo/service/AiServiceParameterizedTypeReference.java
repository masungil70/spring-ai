package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Hotel;

import lombok.extern.slf4j.Slf4j;

/**
 * AI의 응답을 List<Hotel>과 같은 제네릭 컬렉션 타입의 자바 객체로 변환하는 방법을 보여주는 서비스 클래스입니다.
 * 자바의 타입 이레이저(Type Erasure) 문제로 인해 .class 리터럴을 사용할 수 없을 때,
 * ParameterizedTypeReference를 사용하여 완전한 제네릭 타입 정보를 유지하는 방법을 설명합니다.
 */
@Service
@Slf4j
public class AiServiceParameterizedTypeReference {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 클라이언트

  // ##### 생성자 #####
  public AiServiceParameterizedTypeReference(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####

  /**
   * 저수준(Low-Level) API와 ParameterizedTypeReference를 사용하여 AI 응답을 List<Hotel>으로 변환합니다.
   * 제네릭 타입 정보를 유지하면서 변환하는 모든 단계를 수동으로 제어하는 방법을 보여줍니다.
   *
   * @param cities 도시 목록 문자열
   * @return 각 도시의 호텔 정보를 담은 Hotel 객체의 리스트
   */
  public List<Hotel> genericBeanOutputConverterLowLevel(String cities) {
    // 1. BeanOutputConverter를 생성합니다.
    //    자바의 타입 이레이저 때문에 List<Hotel>.class 와 같이 제네릭 타입을 포함한 클래스 리터럴은 사용할 수 없습니다.
    //    대신, ParameterizedTypeReference를 사용하면 런타임에도 List와 Hotel이라는 전체 타입 정보를 유지할 수 있습니다.
    BeanOutputConverter<List<Hotel>> beanOutputConverter = new BeanOutputConverter<>(
        new ParameterizedTypeReference<List<Hotel>>() {});

    // 2. 프롬프트 템플릿을 생성합니다. {format} 플레이스홀더를 포함합니다.
    PromptTemplate promptTemplate = new PromptTemplate("""
        다음 도시들에서 유명한 호텔 3개를 출력하세요.
        {cities}
        {format}
        """);

    // 3. converter.getFormat()을 호출하여 List<Hotel> 구조에 맞는 JSON 스키마 정보를 가져와 프롬프트를 완성합니다.
    Prompt prompt = promptTemplate.create(Map.of(
        "cities", cities, 
        "format", beanOutputConverter.getFormat()));

    // 4. AI를 호출하여 순수한 JSON 텍스트 응답을 받습니다.
    String json = chatClient.prompt(prompt)
        .call()
        .content();
    log.info("AI 원본 응답 (JSON): {}", json);

    // 5. 변환기의 convert() 메소드를 직접 호출하여, 응답 JSON을 List<Hotel> 객체로 변환합니다.
    List<Hotel> hotelList = beanOutputConverter.convert(json);
    return hotelList;
  }
  
  /**
   * 고수준(High-Level) API와 ParameterizedTypeReference를 사용하여 AI 응답을 List<Hotel>으로 변환합니다.
   * 제네릭 컬렉션 타입을 변환하는 가장 간결하고 권장되는 방식입니다.
   *
   * @param cities 도시 목록 문자열
   * @return 각 도시의 호텔 정보를 담은 Hotel 객체의 리스트
   */
  public List<Hotel> genericBeanOutputConverterHighLevel(String cities) {
    // .entity() 메소드에 ParameterizedTypeReference 인스턴스를 전달합니다.
    // 이를 통해 Spring AI 프레임워크가 내부적으로 제네릭 타입을 완벽하게 인지하고,
    // 저수준 API의 모든 복잡한 과정을 자동으로 처리해 줍니다.
    List<Hotel> hotelList = chatClient.prompt().user("""
        다음 도시들에서 유명한 호텔 3개를 출력하세요.
        %s
        """.formatted(cities))
        .call()
        .entity(new ParameterizedTypeReference<List<Hotel>>() {});
    return hotelList;
  }
}
