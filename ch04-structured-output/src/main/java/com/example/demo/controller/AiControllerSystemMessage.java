package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReviewClassification;
import com.example.demo.service.AiServiceSystemMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * AiServiceSystemMessage 서비스를 호출하고, 그 결과를 API 엔드포인트를 통해
 * 외부에 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerSystemMessage {
  // ##### 필드 #####
  @Autowired
  private AiServiceSystemMessage aiService; // System 메시지 로직을 처리하는 서비스
  
  // ##### 메소드 #####
  /**
   * '/ai/system-message' 경로로 들어오는 POST 요청을 처리합니다.
   * 영화 리뷰 텍스트를 받아 해당 리뷰에 대한 분류 결과를 담은 객체를 JSON 형태로 반환합니다.
   *
   * @param review 'review'라는 이름의 요청 파라미터 (사용자가 입력한 리뷰 텍스트)
   * @return 리뷰 분류 정보를 담은 ReviewClassification 객체. Spring MVC에 의해 JSON 객체로 자동 변환되어 응답됩니다.
   */
  @PostMapping(
    value = "/system-message",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.APPLICATION_JSON_VALUE   // 이 엔드포인트는 JSON 형식의 데이터를 생성합니다.
  )
  public ReviewClassification classifyReview(@RequestParam("review") String review) {
    // AI 서비스를 호출하여 리뷰에 대한 분류 결과를 얻습니다.
    ReviewClassification reviewClassification = aiService.classifyReview(review);
    return reviewClassification;
  }
}
