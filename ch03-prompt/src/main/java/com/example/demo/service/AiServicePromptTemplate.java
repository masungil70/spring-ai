package com.example.demo.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiServicePromptTemplate {
    // ##### 필드 #####
    private ChatClient chatClient;
  
    // 1. 시스템 프롬프트 템플릿을 정의합니다.
    private PromptTemplate systemTemplate = SystemPromptTemplate.builder()
        .template("""
            답변을 생성할 때 HTML와 CSS를 사용해서 파란 글자로 출력하세요.
            <span> 태그 안에 들어갈 내용만 출력하세요.
            """)
        .build();  

    // 2. 사용자 프롬프트 템플릿을 정의합니다.  
    private PromptTemplate userTemplate = PromptTemplate.builder()
        .template("다음 한국어 문장을 {language}로 번역해 주세요.\n 문장: {statement}")
        .build();

    // ##### 생성자 #####
    public AiServicePromptTemplate(ChatClient.Builder chatClientBuilder) {
        chatClient = chatClientBuilder.build();
    }

    // ##### 메서드 #####
    //PromptTemplate.create()을 사용하여 프롬프트를 생성합니다.
    public Flux<String> promptTemplate1(String statement, String language) {    
        //1. PrompTemplate(userPrompt)로 prompt를 생성합니다.
        //생성시 바인딩 데이터로 statement, language를 전달합니다   
        log.info("statement: {}, language: {}", statement, language);
        Prompt prompt = userTemplate.create(
            Map.of("statement", statement, "language", language));

        //2. ChatClient의 prompt() 메소드를 호출할 때 prompt 매개값을 전달하였습니다. 
        //실제로 내부적으로 전달되는 것은 userTemplate 입니다 
        Flux<String> response = chatClient.prompt(prompt)
            .stream()
            .content();

        return response;
    }

}