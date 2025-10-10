# Getting Started

1. ch01-spring-ai-project 프로젝트 생성
2. application.properties 파일에 설정 변경 
3. 환경변수에 OPENAI_API_KEY값 설정 
4. / 경로에 대한 controller 와 html 파일 생성 
5. 프론트 UI 이벤트 핸들링
6. AI Chat Rest API 호출 할 수 있게 기본 기능 구현 
7. 라인 단위의 청크 텍스트 전달 방법 
    
    이 용도로 가장 표준적이고 널리 사용되는 MIME 타입은 `text/event-stream` 입니다. 이는 Server-Sent Events (SSE) 기술입니다.

    1. text/plain
    2. Server-Sent Events (SSE)와 text/event-stream (가장 권장되는 방법)
    3. application/x-ndjson (Newline Delimited JSON)

---
각 사항에 따른 예제를 /chunk/ URL 을 실행하고 해당 항목을 클릭하여 예제를 확인해보시면 됩니다 