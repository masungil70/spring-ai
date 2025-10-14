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
    2. ChatClient의 messages() 메소드를 사용하는 방법  
    3. ChatClient와 함께 사용하는 세련된 방식 (.system()과 .user() 활용)

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
