package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiServiceMapOutputConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * AiServiceMapOutputConverter 서비스를 호출하고, 그 결과를 API 엔드포인트를 통해
 * 외부에 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerMapOutputConverter {
  // ##### 필드 #####
  @Autowired
  private AiServiceMapOutputConverter aiService; // MapOutputConverter 로직을 처리하는 서비스
  
  // ##### 메소드 #####
  /**
   * '/ai/map-output-converter' 경로로 들어오는 POST 요청을 처리합니다.
   * 호텔 이름을 받아 해당 호텔의 정보를 AI로부터 추천받아 Map<String, Object> 형태로 반환합니다.
   *
   * @param hotel 'hotel'이라는 이름의 요청 파라미터 (사용자가 입력한 호텔 이름)
   * @return 호텔 정보를 담은 Map 객체. Spring MVC에 의해 JSON 객체로 자동 변환되어 응답됩니다.
   */
  @PostMapping(
    value = "/map-output-converter",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.APPLICATION_JSON_VALUE // 이 엔드포인트는 JSON 형식의 데이터를 생성합니다.
  )
  public Map<String, Object> mapOutputConverter(@RequestParam("hotel") String hotel) {
    // 서비스의 저수준 또는 고수준 API 메소드를 선택하여 호출할 수 있습니다.
    // 아래 두 줄 중 하나를 선택하고 다른 하나를 주석 처리하여 테스트하려는 방식을 쉽게 전환할 수 있습니다.
    Map<String, Object> hotelInfo = aiService.mapOutputConverterLowLevel(hotel);
    // Map<String, Object> hotelInfo = aiService.mapOutputConverterHighLevel(hotel);
    return hotelInfo;
  }
}
