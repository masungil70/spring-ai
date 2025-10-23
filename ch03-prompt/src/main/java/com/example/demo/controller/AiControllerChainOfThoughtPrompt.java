package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiServiceChainOfThoughtPrompt;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * '생각의 사슬(Chain-of-Thought)' AI 서비스와 관련된 웹 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerChainOfThoughtPrompt {
  // ##### 필드 ##### 
  @Autowired
  private AiServiceChainOfThoughtPrompt aiService; // '생각의 사슬' 로직을 처리하는 서비스

  //##### 메소드 ##### 
  /**
   * '/ai/chain-of-thought' 경로로 들어오는 POST 요청을 처리합니다.
   * 사용자의 질문을 받아 '생각의 사슬' 기법으로 답변을 생성하고, 그 과정을 스트리밍으로 반환합니다.
   *
   * @param question 'question'이라는 이름의 요청 파라미터 (사용자 질문)
   * @return AI가 생성하는 답변의 스트림 (Flux<String>). 클라이언트는 이 스트림을 통해 AI의 생각 과정을 실시간으로 받을 수 있습니다.
   */
  @PostMapping(
    value = "/chain-of-thought",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.APPLICATION_NDJSON_VALUE // 이 엔드포인트는 NDJSON(Newline Delimited JSON) 스트림 형식으로 데이터를 생성합니다. 서버-센트 이벤트(SSE)와 유사하게 동작합니다.
  )
  public Flux<String> chainOfThought(@RequestParam("question") String question) {
    // AI 서비스를 호출하여 질문에 대한 답변 스트림을 얻습니다.
    Flux<String> answer = aiService.chainOfThought(question);
    // 얻은 스트림을 클라이언트에게 그대로 반환합니다. Spring WebFlux가 스트리밍 처리를 담당합니다.
    return answer;
  } 
}
