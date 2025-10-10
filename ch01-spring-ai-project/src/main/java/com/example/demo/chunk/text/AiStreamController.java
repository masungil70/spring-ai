package com.example.demo.chunk.text;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chunk")
public class AiStreamController {

    /**
     * [경고] 학습용으로만 사용하세요. 실제 운영 환경에서는 절대 사용하면 안 됩니다.
     * HttpServletResponse를 직접 사용하여 SSE 스트림을 구현합니다.
     * 이 방식은 요청 처리 스레드를 블로킹하여 서버 성능을 심각하게 저하시킵니다.
     * 
     * AI가 답변을 생성하는 것처럼 단어 단위로 스트리밍합니다.
     */
    @GetMapping(value = "/text/sync-ai-response")
    public void streamAiResponse(HttpServletResponse response) {
        // 1. HTTP 응답 헤더를 수동으로 설정합니다.
        // Content-Type을 text/event-stream으로 설정해야 브라우저가 SSE로 인식합니다.
        response.setContentType("text/event-stream");

        // 문자 인코딩을 UTF-8로 설정합니다.
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 프록시나 브라우저가 응답을 캐싱하지 않도록 설정합니다.
        response.setHeader("Cache-Control", "no-cache");
        try (PrintWriter writer = response.getWriter()) {
            String fullResponse = "안녕하세요! HttpServletResponse 직접 구현 예제입니다. [절대사용 금지!]";
            String[] words = fullResponse.split(" ");
            for (String word : words) {
                // 2. SSE 데이터 형식을 정확히 맞춰서 문자열을 작성합니다.
                writer.write("data: " + word + "\n\n");

                // 3. 응답 객체의 버퍼를 강제로 비워 데이터를 즉시 전송합니다.
                // response.flushBuffer() 또는 writer.flush()를 사용합니다.
                response.flushBuffer();

                // 4. [가장 큰 문제점] 요청 처리 스레드를 강제로 대기시킵니다.
                // 이 코드가 바로 이 방식의 가장 큰 문제점입니다. 스트리밍 효과를 내기 위해 현재 요청을
                // 처리 중인 스레드를 잠시 멈춥니다. 이 시간 동안 해당 스레드는 다른 어떤 요청도 처리할 수 없는 유휴 상태가 됩니다.
                // 실제 운영 환경에서는 절대 사용하면 안 됩니다.
                Thread.sleep(150);
            }
            // 5. 스트림 완료 이벤트도 수동으로 작성합니다.
            writer.write("event: completion\n");
            writer.write("data: done\n\n");
            response.flushBuffer();
        } catch (IOException | InterruptedException e) {
            // 스레드 인터럽트가 발생하면 다시 상태를 설정해주는 것이 좋습니다.
            Thread.currentThread().interrupt();
            System.err.println("Error during streaming: " + e.getMessage());
        }
        // 이 메서드는 void를 반환하며, 여기서 응답 처리가 완전히 끝납니다.
    }
}