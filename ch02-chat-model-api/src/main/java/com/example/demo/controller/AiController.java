package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/ai")
@Slf4j
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // HTTP POST 요청을 통해 AI에게 질문을 보낼 수 있는 엔드포인트를 만듭니다.
    @PostMapping(value = "/chat-model", 
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam("question") String question) {
        String answer = aiService.generateText(question);
        log.info("AI 응답: {}", answer);
        return answer;
    }
    
}
