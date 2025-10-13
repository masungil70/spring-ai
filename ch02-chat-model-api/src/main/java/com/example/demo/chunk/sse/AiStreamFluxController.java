package com.example.demo.chunk.sse;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/chunk")
public class AiStreamFluxController {

    /**
    * [권장] ServerSentEvent 객체를 사용하여 구조적으로 SSE를 구현합니다.
    */
    @GetMapping(value = "/sse/ai-response3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamAiResponse() { 
        // 1. 반환 타입을 Flux<ServerSentEvent<String>>으로 변경
        String fullResponse = "안녕하세요!. 무엇을 도와드릴까요? Spring Web Flux의 스트림 예제입니다.";
        String[] words = fullResponse.split(" ");

        Flux<String> wordStream = Flux.fromArray(words)
            .delayElements(Duration.ofMillis(150));

        // 2. 각 단어를 ServerSentEvent 객체로 변환합니다.
        Flux<ServerSentEvent<String>> sseStream = wordStream.map(word -> ServerSentEvent.<String>builder()
            // .event("message") // event 이름을 생략하면 기본값인 'message'가 됩니다.
            .data(word) // .data()를 사용하면 Spring이 "data: " 형식을 만들어줍니다.
            .build()
        );

        // 3. 완료 이벤트도 ServerSentEvent 객체로 생성합니다.
        Flux<ServerSentEvent<String>> completionStream = Flux.just(
            ServerSentEvent.<String>builder()
                .event("completion") // .event()로 이벤트 이름을 명시적으로 지정합니다.
                .data("done")
                .build()
        );

        return Flux.concat(sseStream, completionStream);
    }
}