package com.example.demo.chunk.ndjson;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/chunk")
public class NdjsonStreamController {

   /**
    * 사용자 목록을 NDJSON 형식으로 스트리밍합니다.
    * Content-Type: application/x-ndjson
    */
   @GetMapping(value = "/ndjson/users", produces = MediaType.APPLICATION_NDJSON_VALUE)
   public Flux<User> streamUsers() {
       List<User> users = List.of(
           new User(1L, "Alice", "alice@example.com"),
           new User(2L, "Bob", "bob@example.com"),
           new User(3L, "Charlie", "charlie@example.com"),
           new User(4L, "David", "david@example.com"),
           new User(5L, "Eve", "eve@example.com")
           );
   
           // 1. User 객체 스트림 생성
           return Flux.fromIterable(users)
                      // 각 사용자 객체 전송 사이에 500ms 딜레이를 줍니다.
                      .delayElements(Duration.ofMillis(500));
       }
}