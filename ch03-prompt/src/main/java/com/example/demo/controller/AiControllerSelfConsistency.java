package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiServiceSelfConsistency;

import lombok.extern.slf4j.Slf4j;

/**
 * '자기 일관성(Self-Consistency)' AI 서비스와 관련된 웹 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerSelfConsistency {
  // ##### 필드 ##### 
  @Autowired
  private AiServiceSelfConsistency aiService; // '자기 일관성' 로직을 처리하는 서비스

  // ##### 메소드 #####
  /**
   * '/ai/self-consistency' 경로로 들어오는 POST 요청을 처리합니다.
   * 사용자가 보낸 내용을 '자기 일관성' 기법으로 분류하고 최종 결과를 반환합니다.
   *
   * @param content 'content'라는 이름의 요청 파라미터 (사용자가 분류를 요청한 내용)
   * @return AI 서비스로부터 받은 최종 분류 결과 (예: "중요함")
   */
  @PostMapping(
    value = "/self-consistency",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.TEXT_PLAIN_VALUE // 이 엔드포인트는 일반 텍스트 형식의 데이터를 생성합니다.
  )
  public String selfConsistency(@RequestParam("content") String content) {
    // AI 서비스를 호출하여 내용에 대한 최종 분류 결과를 얻습니다.
    String answer = aiService.selfConsistency(content);
    // 얻은 답변을 클라이언트에게 반환합니다.
    return answer;
  }  
}
