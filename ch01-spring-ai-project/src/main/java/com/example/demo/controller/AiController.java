package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {
    @PostMapping(value = "/chat", 
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam("question") String question) {
        return "아직 모델과 연동되지 않았습니다. 질문: " + question;
    }
    
}
