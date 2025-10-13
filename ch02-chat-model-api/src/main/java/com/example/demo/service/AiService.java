package com.example.demo.service;

import java.util.Optional;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiService {
  // ##### 필드 #####
  @Autowired
  private ChatModel chatModel;

  // ##### 메소드 #####
  public String generateText(String question) {
    log.info("질문: {}", question);
    // 시스템 메시지 생성
    SystemMessage systemMessage = SystemMessage.builder()
        .text("사용자 질문에 대해 한국어로 답변을 해야 합니다.")
        .build();

    // 사용자 메시지 생성
    UserMessage userMessage = UserMessage.builder()
        .text(question)
        .build();
    
    // 대화 옵션 설정
    ChatOptions chatOptions = ChatOptions.builder()
        .model("gpt-4o-mini")
        .temperature(0.3)
        .maxTokens(1000)
        .build();

    // 프롬프트 생성
    Prompt prompt = Prompt.builder()
        .messages(systemMessage, userMessage)
        .chatOptions(chatOptions)
        .build();

    // LLM에게 요청하고 응답받기
    ChatResponse chatResponse = chatModel.call(prompt);
    log.info("응답: {}", chatResponse);
    // 응답에서 생성 결과 추출
    Generation generation = chatResponse.getResult();
    log.info(question + "에 대한 생성 결과: {}", generation);
    // 생성된 어시스턴트 메시지에서 텍스트 추출
    AssistantMessage assistantMessage = generation.getOutput();
    log.info(question + "에 대한 어시스턴트 메시지: {}", assistantMessage);
    // 생성된 텍스트 가져오기
    String answer = assistantMessage.getText();
    log.info("answer = {}", answer);

    // 생성된 텍스트 반환
    return answer;
  }

  public Flux<String> generateStreamText(String question) {
    // 시스템 메시지 생성
    SystemMessage systemMessage = SystemMessage.builder()
        .text("사용자 질문에 대해 한국어로 답변을 해야 합니다.")
        .build();

    // 사용자 메시지 생성
    UserMessage userMessage = UserMessage.builder()
        .text(question)
        .build();

    // 대화 옵션 설정
    ChatOptions chatOptions = ChatOptions.builder()
        .model("gpt-4o")
        .temperature(0.3)
        .maxTokens(1000)
        .build();

    // 프롬프트 생성
    Prompt prompt = Prompt.builder()
        .messages(systemMessage, userMessage)
        .chatOptions(chatOptions)
        .build();

    // LLM에게 요청하고 응답받기
    Flux<ChatResponse> fluxResponse = chatModel.stream(prompt);
    //Flux<String> fluxString = fluxResponse.map(chatResponse -> {
      //AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
      //String chunk = assistantMessage.getText();
      //if (chunk == null) chunk = "";
      //return chunk;
    //}
    Flux<String> fluxString = fluxResponse.map(chatResponse ->
      Optional.ofNullable(chatResponse)         // 1. 가장 바깥쪽 객체부터 Optional로 감쌉니다.
              .map(ChatResponse::getResult)     // 2. chatResponse가 null이 아니면 .getResult()를 실행합니다.
              .map(Generation::getOutput)       // 3. getResult() 결과가 null이 아니면 .getOutput()을 실행합니다.
              .map(AssistantMessage::getText)   // 4. getOutput() 결과가 null이 아니면 .getText()를 실행합니다.
              .orElse("")                 // 5. 최종 결과가 null이면(중간 하나라도 null이었다면) 빈 문자열("")을 반환합니다.
    );
    return fluxString;
  }
}
