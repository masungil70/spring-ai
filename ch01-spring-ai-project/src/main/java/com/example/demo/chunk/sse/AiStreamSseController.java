package com.example.demo.chunk.sse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/chunk")
public class AiStreamSseController {

    /**
     * StreamingResponseBody를 사용하여 수동으로 SSE 스트림을 생성합니다. (비권장)
     * SseEmitter를 사용하는 것이 표준적인 방법입니다.
     * 
     * AI가 답변을 생성하는 것처럼 단어 단위로 스트리밍합니다.
     */
    @GetMapping(value = "/sse/ai-response1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> streamAiResponse() {

        StreamingResponseBody responseBody = outputStream -> {
            // OutputStream에 텍스트를 쉽게 쓰기 위해 PrintWriter를 사용합니다.
            // try-with-resources 구문을 사용하여 자동으로 close 되도록 합니다.
            try (PrintWriter writer = new PrintWriter(outputStream, false, StandardCharsets.UTF_8)) {
                String fullResponse = "안녕하세요!. 이 예제는 StreamingResponseBody로 SSE를 수동 구현한 것입니다.";
                String[] words = fullResponse.split(" ");

                for (String word : words) {
                    // 1. SSE 데이터 형식("data: content\n\n")을 수동으로 작성합니다.
                    writer.write("data: " + word + "\n\n");

                    // 2. 버퍼에 있는 데이터를 즉시 클라이언트로 전송합니다. flush()가 매우 중요합니다.
                    writer.flush();

                    Thread.sleep(150); // 타이핑 효과를 위한 딜레이
                }

                // 3. 스트림 완료를 위한 커스텀 이벤트를 수동으로 작성합니다.
                writer.write("event: completion\n");
                writer.write("data: done\n\n");
                writer.flush();

            } catch (InterruptedException e) {
                System.err.println("Error streaming SSE: " + e.getMessage());
                Thread.currentThread().interrupt(); // 스레드 인터럽트 상태 복원
            }
            // IOException은 Spring이 처리하도록 던져집니다.
        };

        // 4. 수동으로 Content-Type을 text/event-stream으로 설정하여 반환합니다.
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType. TEXT_EVENT_STREAM_VALUE))
                .body(responseBody);
    }
}