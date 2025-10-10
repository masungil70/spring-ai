package com.example.demo.chunk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chunk")
public class MainMenuController {

    @GetMapping("/")
    public String mainMenu() {
        return "/chunk/mainMenu";
    }

        @GetMapping("/text/sync")
    public String syncText() {
        return "/chunk/text/sync-text";
    }

    //비동기 방법(StreamingResponseBody) AI Response (SSE)
    @GetMapping("/sse/step1")
    public String sse_step1() {
        return "/chunk/sse/step1";
    }

    //비동기 방법(SseEmitter) AI Response (SSE)
    @GetMapping("/sse/step2")
    public String sse_step2() {
        return "/chunk/sse/step2";
    }


    //비동기 방법(Web Flux) AI Response (SSE)
    @GetMapping("/sse/step3")
    public String sse_step3() {
        return "/chunk/sse/step3";
    }

    //비동기 방법(Web Flux) AI Response (SSE)
    @GetMapping("/ndjson/step4")
    public String ndjson() {
        return "/chunk/ndjson/step4";
    }

}