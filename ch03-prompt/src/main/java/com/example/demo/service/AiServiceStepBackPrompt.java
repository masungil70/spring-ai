package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Step-Back Prompt 기법을 사용하여 AI 응답을 생성하는 서비스 클래스입니다.
 * 이 기법은 원본 질문에서 한 걸음 물러나 더 넓은 맥락의 질문을 생성하고,
 * 그 질문들에 대한 답변을 바탕으로 원본 질문에 대한 최종 답변을 도출합니다.
 */
@Service
@Slf4j
public class AiServiceStepBackPrompt {
  // ##### 필드 #####
  private ChatClient chatClient; // AI 모델과 상호작용하기 위한 ChatClient 인스턴스

  // ##### 생성자 #####
  public AiServiceStepBackPrompt(ChatClient.Builder chatClientBuilder) {
    // ChatClient 빌더를 통해 ChatClient 인스턴스를 생성합니다.
    chatClient = chatClientBuilder.build();
  }

  // ##### 메소드 #####
  /**
   * Step-Back Prompt 기법을 실행하여 질문에 대한 답변을 반환합니다.
   * @param question 사용자로부터의 원본 질문
   * @return AI가 생성한 최종 답변
   * @throws Exception JSON 파싱 등에서 예외가 발생할 수 있습니다.
   */
  public String stepBackPrompt(String question) throws Exception {
    // 1. 원본 질문을 바탕으로 단계별 질문(step-back questions)을 생성하도록 AI에 요청합니다.
    String questions = chatClient.prompt()
        .user("""
            사용자 질문을 처리하기 Step-Back 프롬프트 기법을 사용하려고 합니다.
            사용자 질문을 단계별 질문들로 재구성해주세요. 
            맨 마지막 질문은 사용자 질문과 일치해야 합니다.
            단계별 질문을 항목으로 하는 JSON 배열로 출력해 주세요.
            예시: ["...", "...", "...", ...]
            사용자 질문: %s
            """.formatted(question))
        .call()
        .content();

    // 단계별 질문(step-back questions)
    log.info("단계별 질문 결과 원본 문자열: {}", questions);
    /*  예시 출력:
    * ```json
    [
        "서울에서 울릉도로 가기 위해 어떤 교통수단을 고려하고 있나요?",
        "각 교통수단의 비용을 비교해본 적이 있나요?",
        "울릉도로 가는 여행 일정은 어떻게 되나요?",
        "예산 범위는 어느 정도인가요?",
        "비용이 가장 적게 드는 방법은 무엇일까요?"   
    ]
    ```

    여기서 주목할 부분은 마지막 질문이 원본 질문과 일치한다는 점입니다.
    */

    // AI 응답에서 JSON 배열 부분만 추출합니다. 
    // JSON 배열은 ```[...]``` 형태로 감싸져 있을 수 있으므로 이를 제거합니다.
    String json = questions.substring(questions.indexOf("["), questions.indexOf("]")+1);
      log.info("단계별 질문(JSON): {}", json);
      
    // JSON 문자열을 List<String> 형태로 변환합니다.
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> listQuestion = objectMapper.readValue(
        json,
        new TypeReference<List<String>>() {}
    );
    
    // 2. 각 단계별 질문에 대한 답변을 순차적으로 구합니다.
    String[] answerArray = new String[listQuestion.size()];
    for(int i=0; i<listQuestion.size(); i++) {
      String stepQuestion = listQuestion.get(i);
      // 이전 단계까지의 답변을 현재 질문의 컨텍스트로 함께 제공합니다.
      String stepAnswer = getStepAnswer(stepQuestion, answerArray);
      answerArray[i] = stepAnswer;

      // 단계별 질문에 대한 답변은 실행 후 로그를 통해 확인할 수 있습니다.
      // 각 단계의 질문과 답변을 로그에 기록합니다.
      // (이해시 중요) 이 부분을 꼭 설명해야 한다 
      log.info("단계{} 질문: {}, 답변: {}", i+1, stepQuestion, stepAnswer);
    }

    // 3. 마지막 질문(원본 질문)에 대한 답변을 최종 결과로 반환합니다.
    return answerArray[answerArray.length-1];
  }

  /**
   * 특정 단계의 질문에 대한 답변을 AI로부터 받아옵니다.
   * @param question 현재 단계의 질문
   * @param prevStepAnswers 이전 단계들에서 얻은 답변들의 배열 (컨텍스트로 활용)
   * @return 현재 질문에 대한 AI의 답변
   */
  public String getStepAnswer(String question, String... prevStepAnswers) {
    String context = "";
    // 이전 단계의 답변들을 하나의 문자열(컨텍스트)로 합칩니다.
    for (String prevStepAnswer : prevStepAnswers) {
      context += Objects.requireNonNullElse(prevStepAnswer, "");
    }
    // 질문과 컨텍스트를 함께 AI에 전달하여 답변을 요청합니다.
    String answer = chatClient.prompt()
        .user("""
            %s
            문맥: %s
            """.formatted(question, context))
        .call()
        .content();
    return answer;
  }
}
