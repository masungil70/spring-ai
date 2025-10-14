# Getting Started

## 사전 작업 순서 

1. ch01-spring-ai-project 프로젝트를 복사하여 ch03-prompt로 붙여넣는다.

2. chunk 관련 기능은 삭제합니다 
```
src 폴더에 있는 chunk 폴더를 삭제합니다 
resources/templates/chunk 폴더를 삭제합니다 
```
3. src/main/resources/applications.properties 파일을 아래와 같이 수정합니다 
```
#spring.application.name=ch01-spring-ai-project
spring.application.name=ch03-prompt

...

```

4. settings.gradle 아래와 같이 수정합니다 
```
rootProject.name = 'ch03-prompt'
```

5. HelloController.java 파일을 HomeController.java로 변경합니다

6. HomeController.java 파일을 아래와 같이 수정합니다 
```
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 이 URL은 prompt-template 실행을 위한 엔드포인트입니다
    @GetMapping("/prompt-template")
    public String promptTemplate() {
        return "prompt-template";
    }  

}

```

7. resources/templates/home.html 파일 아래와 같이 수정합니다 
```
<!DOCTYPE html>
<html>

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Spring AI</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.5/dist/css/bootstrap.min.css" rel="stylesheet" />
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.5/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body>
	<div class="d-flex flex-column vh-100">
	  <div id="headPanel" class="navbar justify-content-between">
	    <a href="/" class="navbar-brand ps-2">
	      <img src="/image/spring_ai_logo_with_text.svg" width="200" />
	    </a>>
	  </div>	
	
	  <div class="card m-2">
			<div class="card-header">
				Ch03. 프롬프트 엔지니어링
			</div>
			<div class="card-body">
				<a href="/prompt-template" class="btn btn-info btn-sm m-1">prompt-template</a>
			</div>
	  </div>
	</div>
</body>

</html>
```

8. resources/templates/prompt-template.html 파일 아래와 같이 수정합니다
   - ch01에서 진행한 home.html 파일을 prompt-template.html으로 복사합니다 
   

9. 위 작업이 완료된 것은 git 이력은 [3장 작업을 시작하기 위해 초기설정]을 checkout하여 작업은 진행하시면 됩니다 

10. 실행 하여 테스트 해봅니다 

    브라우저 주소창에 http://localhost:8080/을 실행하면 아래 그림과 같이 출력됩니다 

    ![alt text](image.png)

    prmopt template 버튼을 클릭하면 아래와 같이 실행되는 것을 확인 할 수 있습니다 

    ![alt text](image-1.png)

---

## PromptTemplate 클래스란 

PromptTemplate: 재사용 가능한 'AI 질문 양식'

PromptTemplate을 한마디로 정의하면, 자주 사용하는 AI 질문의 '틀' 또는 '양식(Template)'이라고 할 수 있습니다.

  가장 쉬운 예로, 우리가 흔히 쓰는 '메일 양식'을 생각해 보세요.

> 제목: {고객명}님, 문의하신 내용에 대한 답변입니다.

>

> 안녕하세요, {고객명}님.

> 문의하신 {문의주제} 건에 대해 답변드립니다.

> ...

> 담당자: {담당자명} 드림


  이 메일 양식만 있으면, 우리는 {고객명}, {문의주제}, {담당자명} 부분만 계속 바꿔가면서 수많은 고객에게 메일을 보낼 수 있습니다. 매번 똑같은 문장을 처음부터 끝까지 작성할 필요가 없죠.

  PromptTemplate이 바로 이런 역할을 합니다. AI에게 보낼 프롬프트(요청문)의 고정된 부분은 '틀'로 만들어두고, 계속 바뀌는 부분은 `{변수}` 형태의 '빈칸'으로 남겨두는 것입니다.

  ---

  왜 PromptTemplate을 사용할까요?

  AI에게 비슷한 패턴의 질문을 반복적으로 해야 할 때, PromptTemplate을 사용하면 다음과 같은 엄청난 장점이 있습니다.

   * 재사용성: 똑같은 프롬프트 구조를 계속 재사용할 수 있어 코드 중복이 사라집니다.
   * 가독성: 프롬프트의 전체 구조와 그 안에서 어떤 부분이 동적으로 바뀌는지 한눈에 파악하기 쉽습니다.
   * 유지보수: 나중에 프롬프트의 문구를 수정해야 할 때, 양식(Template) 한 군데만 고치면 되므로 유지보수가 매우 편리해집니다.

  ---

  가장 직관적인 예제: 'N행시 짓기' AI

  AI에게 특정 주제로 N행시를 짓게 하는 기능을 만든다고 상상해 봅시다. 이때 AI에게 보내는 요청은 항상 비슷한 구조를 가질 겁니다.

  > "너는 N행시의 대가야. {주제} 라는 단어로 {행수}행시를 지어줘."

  이 구조를 PromptTemplate으로 만들어 보겠습니다.

1단계: '질문 양식' 만들기

먼저, 바뀌는 부분인 {주제}와 {행수}를 빈칸으로 둔 '질문 양식'(PromptTemplate)을 만듭니다.

```
import org.springframework.ai.chat.prompt.PromptTemplate;
import java.util.Map;

// 1. {주제}와 {행수}를 빈칸으로 둔 프롬프트 '양식'을 정의합니다.
String templateText = "너는 N행시의 대가야. '{topic}' 이라는 단어로 '{lines}'행시를 지어줘.";
PromptTemplate promptTemplate = new PromptTemplate(templateText);
```
2단계: '빈칸'에 채워 넣을 내용 준비하기

이제 {topic}과 {lines}라는 빈칸에 들어갈 실제 값들을 Map이라는 데이터 구조에 담아 준비합니다.
```
// 2. 'topic' 빈칸에는 "스프링"을, 'lines' 빈칸에는 "2"를 채워 넣을 준비를 합니다.
Map<String, Object> variables = Map.of(
     "topic", "스프링",
     "lines", 2
 );
```   
  중요: Map의 key("topic", "lines")는 양식에 있던 {**빈칸**}의 **이름**과 **정확히 일치**해야 합니다.

  3단계: 양식에 내용을 채워 '완성된 질문' 만들기

  준비된 양식(promptTemplate)과 내용물(variables)을 합쳐서 AI에게 보낼 최종 '완성된 질문'(Prompt)을
  만듭니다. 이 과정을 '렌더링(rendering)'이라고 합니다.
```
// 3. 양식(template)에 내용(variables)을 채워서 최종 프롬프트 객체를 생성합니다.
Prompt finalPrompt = promptTemplate.create(variables);
```

4단계: 결과 확인

finalPrompt 안에는 어떤 내용이 들어있을까요? create 메소드가 {topic}과 {lines}를 우리가 준비한 값으로 모두 바꿔치기해서 다음과 같은 완성된 질문을 만들어 줍니다.

```
// finalPrompt.getContents() 를 출력해보면 나오는 최종 질문 내용
```
"너는 N행시의 대가야. '스프링' 이라는 단어로 '2'행시를 지어줘."

이제 이 finalPrompt를 ChatClient나 ChatModel에 넘겨주기만 하면 AI가 2행시를 지어주겠죠!

재사용성의 마법:

만약 3행시를 짓고 싶다면? 양식을 또 만들 필요 없이, 내용물만 바꿔서 재사용하면 됩니다.
```
// 내용물만 바꿔서 양식을 재사용!
Map<String, Object> variables2 = Map.of(
    "topic", "인공지능",
    "lines", 3
);

// 똑같은 promptTemplate을 사용해서 새로운 프롬프트를 생성
Prompt finalPrompt2 = promptTemplate.create(variables2);

// 결과: "너는 N행시의 대가야. '인공지능' 이라는 단어로 '3'행시를 지어줘."
```

핵심 정리:

PromptTemplate은 반복되는 AI 요청문을 '양식'으로 만들어두고, 그 안의 '빈칸'만 바꿔 끼워가며 효율적으로 프롬프트를 생성하게 해주는 매우 유용한 도구입니다.

---

### 왜 SystemPromptTemplate을 사용할까요?

AI에게 역할을 부여하는 '시스템 프롬프트'와 사용자의 '질문 프롬프트'를 분리하면 다음과 같은 장점이 있습니다.

* 역할과 질문의 분리: AI의 정체성(예: "너는 번역가야")과 사용자의 질문("이것을 번역해 줘")을 명확히 나눌 수 있어 코드가 깔끔해집니다.

* 동적인 역할 부여: 시스템 프롬프트 자체도 템플릿으로 만들면, AI의 역할을 동적으로 계속 바꿀 수 있습니다. (예: {언어} 번역가, {직업} 전문가)

---

### 시나리오: '다국어 번역기' AI 만들기

이번에는 AI를 '지정한 언어로 번역해 주는 번역기'로 만드는 시나리오를 사용하겠습니다.

   * 시스템 프롬프트 양식: "답변을 생성할 때 HTML와 CSS를 사용해서 파란 글자로 출력하세요.<span> 태그 안에 들어갈 내용만 출력하세요."
   * 사용자 프롬프트 양식: "다음 한국어 문장을 {language}로 번역해주세요.\n 문장: {statement}"

이제 이 두 가지 템플릿을 조합하는 3가지 방법을 살펴보겠습니다.

템플릿을 사용하는 방법

    1. 가장 기본적인 수동 방식 (각각의 메시지를 생성 후 조합)
    2. PromptTemplat.createMessage() 메소드를 프롬프트를 생성방법을 확인
    3. ChatClient의 prompt(), messages() 메소드를 사용하는 방법  
    4. ChatClient와 함께 사용하는 세련된 방식 (.system()과 .user() 활용)

---
### 1. PromptTemplate을 사용하기 위한 서비스 클래스 구조 
```
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
      .template("다음 한국어 문장을 {language}로 번역해주세요.\n 문장: {statement}")
      .build();

  // ##### 생성자 #####
  public AiServicePromptTemplate(ChatClient.Builder chatClientBuilder) {
    chatClient = chatClientBuilder.build();
  }
}
```

### 1. 가장 기본적인 수동 방식 (각각의 메시지를 생성 후 조합)
PromptTemplate.create()을 사용하여 프롬프트를 생성합니다.
```
  // ##### 메소드 #####
  public Flux<String> promptTemplate1(String statement, String language) {    
    
    //PrompTemplate(userPrompt)로 prompt를 생성합니다.
    //생성시 바인딩 데이터로 statement, language를 전달합니다   
    Prompt prompt = userTemplate.create(
        Map.of("statement", statement, "language", language));

    //ChatClient의 prompt() 메소드를 호출할 때 prompt 매개값을 전달하였습니다. 
    //실제로 내부적으로 전달되는 것은 userTemplate 입니다 
    Flux<String> response = chatClient.prompt(prompt)
        .stream()
        .content();

    return response;
  }
```

위 방법은 간단하게 userTemplate만 사용하여 LLM을 호출하는 경우에 사용하면 좋습니다


---
### AiControllerPromptTemplate 클래스에서 AiServicePromptTemplate 서비스에서 사용
```
package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiServicePromptTemplate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerPromptTemplate {

    // ##### 필드 #####
    @Autowired
    private AiServicePromptTemplate aiService;

    // ##### 요청 매핑 메소드 #####
    @PostMapping(value = "/prompt-template", 
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> promptTemplate(
        @RequestParam("statement") String statement,
        @RequestParam("language") String language
        ) {
        return aiService.promptTemplate1(statement, language);
    }
    
}

```
---

### prompt-template.html에 언어 선택UI와 /prompt-template 엔드포인트를 호출 할 수 있게 수정함 
언어 선택UI 추가 
```
...
    <div id="inputPanel" class="bg-secondary-subtle">
      <!-- 언어 선택 시작 -->
      <div class="input-group p-2">
        <span class="input-group-text">언어</span>
        <select id="language" class="form-control">
          <option value="독일어">독일어</option>
          <option value="중국어">중국어</option>
          <option value="일본어">일본어</option>
          <option value="영어" selected>영어</option>
        </select>
      </div>
      <!-- 언어 선택 끝 -->
      <!-- 질문 입력 패널 시작 -->
      <div class="input-group p-2">
        <span class="input-group-text">질문</span>
...
```

/prompt-template 엔드포인트를 호출할 수 있게 수정
```
...
      try {
        // 텍스트 질문을 얻고 대화 패널에 추가하기
        const question = document.getElementById("question").value;
        if (question === "") return;
        springai.addUserQuestion(question, "chatPanel");

        // 응답이 오기까지 스피너 보여주기
        springai.setSpinner("spinner", true);

        // AJAX 요청하고 응답받기
        const language = document.getElementById("language").value;
        const response = await fetch('/ai/prompt-template', {
          method: "post",
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded',
						'Accept': 'application/x-ndjson' //라인으로 구분된 청크 데이터
					},					
          body: new URLSearchParams({ "statement":question, language })
        });
        // AI 모델 답변이 들어갈 위치를 대화 패널에 추가
        const uuid = springai.addAnswerPlaceHolder("chatPanel");
        
        // 텍스트 답변 출력하기
        springai.printAnswerStreamText(response.body, uuid, "chatPanel");

      } catch (error) {
        console.log(error);
      } finally {
        //스피너 숨기기
        springai.setSpinner("spinner", false);
      }
...
```
---
### 브라우저에서 실행 하여 테스트 해보기 

    브라우저 주소창에 http://localhost:8080/prompt-template을 실행하고 언어를 변경 하고 제출을 하면 아래 그림과 같이 출력됩니다 

    ![alt text](image-2.png)

---
### PromptTemplat.createMessage() 메소드를 사용하여 프롬프트를 생성방법을 확인 
```
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
```
---
### 3. ChatClient클래스의 prompt(), messages() 메소드를 사용하여 프롬프트 생성 방법
```
  // ChatClient.prompt() 메소드, messages() 메소드를 사용하여 프롬프트 템플릿 객체를 전달하여 프롬프트를 생성합는 방법입니다 
  
  public Flux<String> promptTemplate3(String statement, String language) {    
    Flux<String> response = chatClient.prompt()
        .messages(
            systemTemplate.createMessage(),
            userTemplate.createMessage(Map.of("statement", statement, "language", language)))
        .stream()
        .content();
    return response;
  }  

```
---
### 4. ChatClient와 함께 사용하는 세련된 방식 (.system()과 .user() 활용)
```
  public Flux<String> promptTemplate4(String statement, String language) {    
    Flux<String> response = chatClient.prompt()
        .system(systemTemplate.render())
        .user(userTemplate.render(Map.of("statement", statement, "language", language)))
        .stream()
        .content();
    return response;
  }   
```
---
### 5. String.formatted() 메소드를 사용하여 프롬프트를 생성합니다.
```
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

```
---
## 3.2 복수 메시지 추가 
- LLM에 요청할 때 하나의 SystemMessage와 하나의 UserMessage만 프로프트에 포함되는 것은 아님 
- 한개의 SystemMessage와 여러 개의 UserMessage, 여러 개의 AssistantMessage도 같이 포함될 수 있음 
- 대표적인 예로 대화 기록을 유지 하기, 이전 대화 내용(UserMessage + AssistantMessage) 전체를 프롬트드에 포함 시킬 수 있음 

### serivce/AiServiceMultiMessage.java 
기능 설명 

1. 시스템 메시지(System Message) 생성
  * SystemMessage는 AI 모델에게 역할을 부여하거나 행동 지침을 내리는 특별한 메시지입니다.
  * 여기서는 "당신은 AI 비서입니다. 지난 대화 내용을 참고해서 답변해주세요." 라는 지침을 설정하여, AI가 단순히 질문에만 답하는 게 아니라 이전 대화의 문맥을 파악하여 답변하도록 유도합니다.

2. 대화 초기화
  * if(chatMemory.size() == 0): chatMemory 리스트의 크기가 0이라는 것은 대화가 막 시작되었음을 의미합니다.
  * 이때만 SystemMessage를 chatMemory에 추가합니다. 이렇게 하면 전체 대화 세션 동안 AI는 설정된 역할을 유지하게 됩니다.

3. LLM 요청 및 응답 (Spring AI `ChatClient` 사용)
  * chatClient.prompt(): LLM에 보낼 프롬프트(요청)를 구성하기 시작합니다.
  * .messages(chatMemory): 가장 중요한 부분입니다. chatMemory에 저장된 이전 대화 기록 전체(SystemMessage 포함)를 요청에 추가합니다.
  * .user(question): 그 뒤에 현재 사용자의 질문을 추가합니다.
  * 결과적으로 [시스템 메시지, 이전 사용자 질문1, 이전 AI 답변1, 이전 사용자 질문2, ..., 현재 사용자 질문] 형태의 대화 목록이 LLM에 전달됩니다.
  * .call().chatResponse(): 구성된 프롬프트를 LLM에 보내고, 응답이 올 때까지 기다린 후(call()), 전체 메타데이터를 포함한 ChatResponse 객체로 응답을 받습니다.

4. 대화 기록 업데이트 (상태 관리)
* LLM으로부터 답변을 받은 후, 방금 사용자가 질문한 내용(UserMessage)과 AI가 답변한 내용(AssistantMessage)을 chatMemory 리스트에 추가합니다.
* 이렇게 chatMemory를 계속 업데이트해야 다음번 질문을 할 때 방금 나눈 대화까지 포함하여 문맥을 유지할 수 있습니다. 이 chatMemory 객체는 컨트롤러 계층에서 세션 등을 통해 관리 해야 합니다 

5. 결과 반환
  * chatResponse에서 AI의 답변에 해당하는 AssistantMessage를 꺼내고, 그 안에서 실제 텍스트 내용(getText())을 추출하여 반환합니다. 이 문자열이 사용자에게 보여질 최종 답변이 됩니다.

요약 및 핵심 포인트

* 상태 유지(Stateful) 대화: 이 서비스는 chatMemory라는 List를 통해 이전 대화 기록을 계속 유지하고 다음 요청에 활용함으로써, 단발성 질의응답이 아닌 연속적인 대화를 가능하게 합니다.
* 역할 기반 프롬프팅: SystemMessage를 이용해 AI 모델의 페르소나(AI 비서)와 행동 방식을 지정하여 더 일관되고 원하는 방향의 답변을 얻습니다.
* Spring AI 추상화: 복잡한 API 호출 과정을 ChatClient라는 객체와 .prompt()...call()과 같은 직관적인 메소드 체이닝으로 매우 간단하게 처리하고 있습니다. 개발자는 HTTP 요청/응답이나 JSON 파싱에 신경 쓸 필요가 없습니다.

**동기 방식**으로 구현한 이유는 로직을 간단하고 쉽게 이해 할 수 있게 하기 위해서 입니다.

---
### controller/AiControllerMultiMessage.java
이 클래스는 사용자의 웹 요청을 받아 AiServiceMultiMessages 서비스와 연결해주는 스프링
컨트롤러(Controller) 입니다. 이 컨트롤러의 가장 중요한 역할은 HTTP 세션(Session)을 이용해
사용자별 대화 기록을 관리하는 것입니다.

``` java
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerMultiMessages {
  // ##### 필드 ##### 
  @Autowired
  private AiServiceMultiMessages aiService;
  
  // ##### 요청 매핑 메소드 #####
  @PostMapping(
    value = "/multi-messages",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  public String multiMessages(
      @RequestParam("question") String question, HttpSession session) {
    
    // 1. 세션에서 대화 기록 가져오기
    List<Message> chatMemory = (List<Message>) session.getAttribute("chatMemory");
    // 2. 대화 기록이 없으면 새로 생성
    if(chatMemory == null) {
      chatMemory = new ArrayList<Message>();
      session.setAttribute("chatMemory", chatMemory);
    }
    // 3. 서비스 호출하여 답변 받기
    String answer = aiService.multiMessages(question, chatMemory);
    
    // 4. 답변 반환  }
}

```

---
## 3.3 디폴트 메시지와 옵션 

디폴트 메시지와 옵션은 ChatClient 인스턴스를 생성할 때, 해당 클라이언트를 통해 나가는 모든 요청에 일관된 규칙과 설정을 적용하기 위해 사용됩니다. ChatClient를 생성할 때 디폴트 메시지와 옵션을 설정하면, LLM을 요청할 때 생략(메시지, 옵션)할 수 있습니다.


|메소드|설명|
|---|---|
|defaultSystem()| 기본 SystemMessage를 추가 |
|defaultUser()| 기본 UserMessage를 추가 |
|defaultOptions()| 기본 대화옵션을 설정|

 시나리오: "긍정 에너지 뿜뿜! 챗봇" 만들기

  우리가 만들고 싶은 챗봇의 규칙은 다음과 같다고 가정해 봅시다.

   1. 페르소나(Persona): 적절한 감탄사, 웃음등을 넣어서 친절하게 대화를 해주세요..
   2. 언어: 모든 답변은 반드시 한국어로 해야 한다.
   3. 창의성: 약간 창의적이고 다양한 답변을 생성해야 한다. (너무 딱딱하지 않게)
   4. 토큰 : 너무 긴 답변을 하지 않도록 최대토큰 수를 300으로 설정한다
   5. 모델: 비용 절약을 위해 gpt-3.5-turbo 모델을 사용한다.

  이 규칙들을 모든 AI 요청마다 반복해서 설정하는 것은 번거롭고 실수의 여지가 있습니다. 이때 디폴트 메시지와 옵션을 사용하면 이 규칙들을 단 한 번만 설정할 수 있습니다.

service/AiServiceDefaultMethod.java 

```java

@Service
@Slf4j
public class AiServiceDefaultMethod {
  // ##### 필드 #####
  private ChatClient chatClient;

  // ##### 생성자 #####
  public AiServiceDefaultMethod(ChatClient.Builder chatClientBuilder) {
    chatClient = chatClientBuilder
        .defaultSystem("""
          적절한 감탄사, 웃음등을 넣어서 친절하게 대화를 해주세요.
          모든 답변은 반드시 한국어로 해야 합니다.
          """)
        .defaultOptions(ChatOptions.builder()
            .temperature(1.0)
            .maxTokens(300)
            .model("gpt-3.5-turbo")
            .build())
        .build();
  }

  // ##### 메소드 #####
  public Flux<String> defaultMethod(String question) {   
    Flux<String> response = chatClient.prompt()
        .user(question)
        .stream()
        .content();
    return response;
  }
}
```

controller/AiControllerDefaultMethod.java 
```java

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiControllerDefaultMethod {
  // ##### 필드 ##### 
  @Autowired
  private AiServiceDefaultMethod aiService;
  
  // ##### 메소드 #####
  @PostMapping(
    value = "/default-method",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.APPLICATION_NDJSON_VALUE
  )
  public Flux<String> defaultMethod(@RequestParam("question") String question) {
    return aiService.defaultMethod(question);
  }
}

```

브라우저에서 실행 하여 테스트 해보기
http://localhost:8080/default-method을 실행하고 제출을 하면 아래 그림과 같이 출력됩니다 

![alt text](image-3.png)
---

3.4 프롬프트 엔지니어링이란?

프롬프트 엔지니어링은 AI 모델(특히 LLM)로부터 원하는 최상의 결과를 얻어내기 위해, 모델에 입력하는 질문이나 지시어(프롬프트)를 최적화하는 기술이자 과정입니다. 단순히 질문을 던지는 것을 넘어, AI가 더 정확하고, 창의적이며, 일관된 답변을 생성하도록 유도하는 'AI와의 소통법'이라고 할 수 있습니다.

프롬프트 엔지니어링 기본 기법

| 기법 (Technique) | 설명| 핵심 목표| 예시 (Bad Prompt → Good Prompt)|
|:---|:---|:---|:---|
|1. 명확하고 구체적으로 지시하기 | 모호한 표현을 피하고, 원하는 결과물에 대한 상세한 맥락, 조건, 요구사항을 명시적으로 제공합니다. | AI의 추측을 최소화하고, 의도에 가장 가까운 정확한 답변을 유도합니다.| Bad: "스프링에 대해 알려줘."<br>Good: "Java 웹 개발 초보자를 위해, 스프링 프레임워크의 핵심 특징 3가지를 설명하고, 각 특징이 왜 중요한지 간단한 예시와 함께 알려줘.|
|2. 역할 지정하기 (Persona Pattern) | AI에게 특정 전문가, 인물, 캐릭터 등의 역할을 부여하여 그 역할에 맞는 톤과 스타일, 지식 수준으로 답변하게 합니다. | 답변의 스타일, 어조, 전문성 수준을 제어하여 특정 목적에 맞는 결과물을 얻습니다. | Bad: "이 코드를 리뷰해줘."<br>Good: "당신은 10년차 시니어 백엔드 개발자입니다. 아래 Java 코드의 성능, 가독성, 잠재적 버그 관점에서 리뷰하고 개선점을 제안해주세요."|
|3. 예시 제공하기 (Few-Shot Prompting)|AI가 따라야 할 입/출력의 예시(1개~여러 개)를 프롬프트에 포함시켜, 원하는 결과물의 형식과 패턴을 학습시킵니다.| 복잡하거나 새로운 형식의 작업을 AI에게 가르치고, 결과물의 일관성을 높입니다. | Bad: "이 문장이 긍정인지 부정인지 판단해줘: '음식이 너무 맛없어요.'"<br>Good: "다음 예시처럼 문장의 감성을 분석해줘.<br>예시1) 문장: "이 영화 정말 최고야!" -> 감성: 긍정<br>예시2) 문장: "기다리다 지쳤어요." -> 감성: 부정<br><br>이제 이걸 분석해줘.<br>문장: "음식이 너무 맛없어요." -> 감성:"|
|4. 출력 형식 지정하기 (Specify Output Format) | AI가 답변이 어떤 형식(JSON, Markdown, XML, 리스트 등)으로 생성되어야 하는지 명확하게 지정합니다. | 프로그램이 후처리하기 쉬운 구조화된 데이터를 얻거나, 가독성 높은 결과물을 확보합니다. | Bad: "바나나, 사과, 오렌지의 장단점을 알려줘."<br>Good: "바나나, 사과, 오렌지의 장단점을 아래와 같은 마크다운 테이블 형식으로 정리해줘.|
|5. 단계별로 생각하게 유도하기 (Chain of Thought)|복잡한 문제에 대해 AI가 최종 답변을 내리기 전에, 문제 해결 과정이나 논리적 단계를 스스로 생각하고 서술하도록 유도합니다.|수학, 논리 추론 등 복잡한 문제의 정확도를 크게 향상시키고, 답변의 근거를 투명하게 확인합니다.|Bad: "사과 5개를 3명이 나눠 가지면 한 명당 몇 개를 가질 수 있나?"<br>Good: "사과 5개를 3명이 나눠 가지면 한 명당 몇 개를 가질 수 있는지 계산해줘. 단계별로 생각해서 설명해줘."|
|6. 구분자 사용하기 (Use Delimiters) |지시문, 맥락(context), 입력 데이터 등 프롬프트의 각 부분을 명확하게 분리하기 위해 구분자(예: """, ###, <tag>)를 사용합니다.|AI가 지시문과 처리해야 할 데이터를 혼동하는 것을 방지하고, 프롬프트의 구조를 명확하게 합니다. |Bad: "아래 글을 요약해줘. 인공지능은..."<br>Good: "당신이 요약해야 할 텍스트는 세 개의 따옴표(""")로 둘러싸여 있습니다.<br><br>"""<br>인공지능은 인간의 학습능력, 추론능력, 지각능력 등을 인공적으로 구현한 컴퓨터 시스템이다...<br>"""<br><br>위 텍스트를 한 문장으로 요약해줘."|
|7. 부정적 표현보다 긍정적 지시 사용하기|"~하지 마라"는 부정적인 지시보다는 "~해라"는 긍정적이고 직접적인 지시를 사용합니다. | AI가 부정적인 지시를 놓치거나 잘못 해석할 가능성을 줄이고, 원하는 행동을 더 확실하게 유도합니다.| Bad: "전문 용어를 사용하지 말고 설명해줘."<br>Good: "초등학생도 이해할 수 있도록 쉬운 단어를 사용해서 설명해줘." |


이러한 기법들은 단독으로 사용될 수도 있고, 여러 기법을 조합하여 더 정교하고 효과적인 프롬프트를 구성할 수도 있습니다. 좋은 프롬프트를 만드는 것은 원하는 결과를 얻기 위한 반복적인 실험과 개선의 과정입니다.


프롬프트 엔지니어링 6대 핵심 기법

  1. 제로샷 프롬프팅 (Zero-Shot Prompting)
  2. 연쇄적 사고 프롬프팅 (Chain of Thought - CoT)
  3. 역할 부여 프롬프팅 (Role-Playing Prompting)
  4. 스탭-백 프롬프트(Step-back Prompting)
  5. 생각의 사슬 프롬프팅 (Chain of Thought - CoT)
  6. 자기 일관성 기법 (Self-Consistency)

---
