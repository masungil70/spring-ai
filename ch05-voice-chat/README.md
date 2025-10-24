# Getting Started

## 사전 작업 순서 

1. ch03-prompt 프로젝트를 복사하여 ch05-voice-chat로 붙여넣는다.

2. src/main/resources/applications.properties 파일을 아래와 같이 수정합니다 
```
#spring.application.name=ch03-prompt
spring.application.name=ch05-voice-chat

...

```

3. settings.gradle 아래와 같이 수정합니다 
```
rootProject.name = 'ch05-voice-chat'
```

4. src의 controller 폴더에 Ai로 시작하는 Controller java 파일을 모두 삭제합니다

5. src의 service 폴더에 Ai로 시작하는 Service java 파일을 모두 삭제합니다

6. resources/templates 폴더에 home.html 파일을 제외하고 모두 삭제합니다

7. resources/templates/home.html 파일은 아래와 같이 수정합니다 
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
				Ch05. 음성 대화, STT / TTS
			</div>
			<div class="card-body">
			</div>
	  </div>
	</div>
</body>

</html>
```

8. 위 작업이 완료된 것은 git 이력은 [5장 작업을 시작하기 위해 초기설정]을 checkout하여 작업은 진행하시면 됩니다 

9. 실행 하여 테스트 해봅니다 

    브라우저 주소창에 http://localhost:8080/을 실행하면 아래 그림과 같이 출력됩니다 

	![alt text](image.png)

---

## 5.1 구조화된 출력 변환기 

