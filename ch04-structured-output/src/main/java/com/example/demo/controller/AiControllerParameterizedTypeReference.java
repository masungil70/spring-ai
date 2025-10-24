package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Hotel;
import com.example.demo.service.AiServiceParameterizedTypeReference;

import lombok.extern.slf4j.Slf4j;

/**
 * AiServiceParameterizedTypeReference 서비스를 호출하고, 그 결과를 API 엔드포인트를 통해
 * 외부에 제공하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerParameterizedTypeReference {
  // ##### 필드 #####
  @Autowired
  private AiServiceParameterizedTypeReference aiService; // ParameterizedTypeReference 로직을 처리하는 서비스

  //##### 메소드 #####
  /**
   * '/ai/generic-bean-output-converter' 경로로 들어오는 POST 요청을 처리합니다.
   * 여러 도시 이름을 받아 각 도시의 호텔 정보를 AI로부터 추천받아 List<Hotel> 형태로 반환합니다.
   *
   * @param cities 'cities'라는 이름의 요청 파라미터 (사용자가 입력한 도시 이름들)
   * @return 여러 호텔 정보를 담은 Hotel 객체의 리스트(List<Hotel>). Spring MVC에 의해 JSON 배열로 자동 변환되어 응답됩니다.
   */
  @PostMapping(
    value = "/generic-bean-output-converter",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, // 이 엔드포인트는 form-urlencoded 형식의 데이터를 소비합니다.
    produces = MediaType.APPLICATION_JSON_VALUE  // 이 엔드포인트는 JSON 형식의 데이터를 생성합니다.
  )
  public List<Hotel> genericBeanOutputConverter(@RequestParam("cities") String cities) {
    // 서비스의 저수준 또는 고수준 API 메소드를 선택하여 호출할 수 있습니다.
    // 아래 두 줄 중 하나를 선택하고 다른 하나를 주석 처리하여 제네릭 타입을 다루는
    // 두 가지 방식의 구현을 쉽게 전환하며 테스트할 수 있습니다.
    //List<Hotel> hotelList = aiService.genericBeanOutputConverterLowLevel(cities);
    List<Hotel> hotelList = aiService.genericBeanOutputConverterHighLevel(cities);
    return hotelList;
  }
}
