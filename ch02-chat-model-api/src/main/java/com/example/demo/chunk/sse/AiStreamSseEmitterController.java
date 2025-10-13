package com.example.demo.chunk.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/chunk")
public class AiStreamSseEmitterController {

    // 이벤트를 보낼 작업을 처리할 별도의 스레드 풀을 생성합니다.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * SseEmitter를 사용하여 AI 답변을 스트리밍합니다. (Non-WebFlux)
     * AI가 답변을 생성하는 것처럼 단어 단위로 스트리밍합니다.
     * Content-Type: text/event-stream
     */
    @GetMapping(value = "/sse/ai-response2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAiResponse() {
        // 1. SseEmitter 객체 생성 (타임아웃을 매우 길게 설정하여 연결 유지)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 별도의 스레드에서 데이터 전송 로직 실행
        executor.execute(() -> {
            try {
                String fullResponse = "안녕하세요!. 무엇을 도와드릴까요? Spring MVC의 SseEmitter 예제입니다.";
                String[] words = fullResponse.split(" ");

                for (String word : words) {
                    // 3. SseEmitter.SseEventBuilder를 사용하여 이벤트 생성 및 전송
                    emitter.send(SseEmitter.event().data(word));

                    // 4. 단어 사이에 약간의 지연을 추가하여 스트리밍 효과를 줍니다.
                    Thread.sleep(150); 
                }

                // 5. 스트림 완료를 위한 커스텀 이벤트 전송
                emitter.send(SseEmitter.event().name("completion").data("done"));

            } catch (IOException | InterruptedException e) {
                // 6. 오류 발생 시 emitter를 통해 오류 전파
                emitter.completeWithError(e);
            } finally {
                // 7. 모든 데이터 전송 후 emitter 완료 처리
                emitter.complete();
            }
        });

        // 7. emitter 객체를 즉시 반환
        return emitter;
    }
}