package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiServiceStepBackPrompt;

import lombok.extern.slf4j.Slf4j;

/**
 * Step-Back Prompt 기법을 사용하는 AI 서비스와 관련된 웹 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerStepBackPrompt {
  // ##### 필드 ##### 
  @Autowired
  private AiServiceStepBackPrompt aiService; // Step-Back Prompt 로직을 처리하는 서비스
  
  /**
   * '/ai/step-back-prompt' 경로로 들어오는 POST 요청을 처리합니다.
   * 사용자의 질문을 받아 Step-Back Prompt 기법을 통해 답변을 생성하고 반환합니다.
   *
   * @param question 'question'이라는 이름의 요청 파라미터 (사용자 질문)
   * @return AI 서비스로부터 생성된 텍스트 답변
   * @throws Exception AiService에서 예외가 발생할 경우
   */
  @PostMapping(
    value = "/step-back-prompt",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.TEXT_PLAIN_VALUE  // 이 엔드포인트는 일반 텍스트 형식의 데이터를 생성합니다.
  )
  public String stepBackPrompt(@RequestParam("question") String question) throws Exception {
    // AI 서비스를 호출하여 질문에 대한 답변을 얻습니다.
    String answer = aiService.stepBackPrompt(question);
    // 얻은 답변을 클라이언트에게 반환합니다.
    return answer;
  }
}
