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

