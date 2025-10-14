package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
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
    //1. PromptTemplate.create()을 사용하여 프롬프트를 생성합니다.
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


    // 2. PromptTemplate.createMessage() 메소드를 사용하여 프롬프트를 생성방법을 확인
    // ##### 메소드 #####
    public Flux<String> promptTemplate2(String statement, String language) {    
        // 1. 각 템플릿의 빈칸을 채울 값들을 Map으로 준비합니다.
        Map<String, Object> userVariables = Map.of("statement", statement, "language", language);

        // 2. 각 템플릿을 Message 객체로 렌더링합니다.
        //    주의: .create()가 아닌 .createMessage()를 사용해야 Message 타입으로 반환됩니다.
        Message systemMessage = systemTemplate.createMessage();
        Message userMessage = userTemplate.createMessage(userVariables);

        // 3. 생성된 메시지들을 리스트에 담아 최종 Prompt 객체를 만듭니다.
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 4. 완성된 Prompt를 ChatClient에 전달하여 AI를 호출합니다.
        Flux<String> response = chatClient.prompt(prompt)
            .stream()
            .content();

        return response;
    }          

    // 3. ChatClient.prompt() 메소드, messages() 메소드를 사용하여 프롬프트 템플릿 객체를 전달하여 프롬프트를 생성합는 방법입니다 
    public Flux<String> promptTemplate3(String statement, String language) {    
        Flux<String> response = chatClient.prompt()
            .messages(
                systemTemplate.createMessage(),
                userTemplate.createMessage(Map.of("statement", statement, "language", language)))
            .stream()
            .content();
        return response;
    }  

    // 4. ChatClient와 함께 사용하는 세련된 방식 (.system()과 .user() 활용)
    public Flux<String> promptTemplate4(String statement, String language) {    
        Flux<String> response = chatClient.prompt()
            .system(systemTemplate.render())
            .user(userTemplate.render(Map.of("statement", statement, "language", language)))
            .stream()
            .content();
        return response;
    }   

    // 5. String.formatted() 메소드를 사용하여 프롬프트를 생성합니다.
    public Flux<String> promptTemplate5(String statement, String language) {    
        String systemText = """
            답변을 생성할 때 HTML와 CSS를 사용해서 파란 글자로 출력하세요.
            <span> 태그 안에 들어갈 내용만 출력하세요.
            """;
        String userText = """
            다음 한국어 문장을 %s로 번역해주세요.\n 문장: %s
            """.formatted(language, statement);
        
        Flux<String> response = chatClient.prompt()
            .system(systemText)
            .user(userText)
            .stream()
            .content();
        return response;
    }     
}