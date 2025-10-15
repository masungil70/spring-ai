// ############################################################################
// 텍스트 대화와 관련된 코드
// ############################################################################
window.springai = window.springai || {};


// ##### 사용자 질문을 보여줄 엘리먼트를 채팅 패널에 추가하는 함수 #####
springai.addUserQuestion = function (question, chatPanelId) {
  const html = `
    <div class="d-flex justify-content-end m-2">
      <table>
        <tr>
          <td><img src="/image/user.png" width="30"/></td>
          <td><span>${question}</span></td>
        </tr>
      </table>
    </div>
  `;
  document.getElementById(chatPanelId).innerHTML += html;

  //채팅 패널의 스크롤을 제일 아래로 내려주는 함수 호출 한다
  springai.scrollToHeight(chatPanelId);
};


// ##### 응답을 보여줄 엘리먼트를 채팅 패널에 추가하는 함수 #####
springai.addAnswerPlaceHolder = function (chatPanelId) {
  //id-를 붙이는 이유: 숫자로 시작하면 CSS 선택자 문법 에러 날 수 있음
  let uuid = "id-" + crypto.randomUUID();
  let html = `
    <div class="d-flex justify-content-start border-bottom m-2">
      <table>
        <tr>
          <td><img src="/image/assistant.png" width="50"/></td>
          <td><span id="${uuid}"></span></td>
        </tr>
      </table>       
    </div>
  `;
  document.getElementById(chatPanelId).innerHTML += html;
  return uuid;
};


// ##### 텍스트 응답을 출력하는 함수 #####
springai.printAnswerText = async function (responseBody, targetId, chatPanelId) {
    // 스트리밍 텍스트 응답을 출력하는 함수 호출로 변경합니다 
    springai.printAnswerStreamText(responseBody, targetId, chatPanelId);
}

// ##### 스트리밍 텍스트 응답을 출력하는 함수 #####
springai.printAnswerStreamText = async function (responseBody, targetId, chatPanelId) {
    //스트리밍 응답 처리
    // 출력 대상 엘리먼트을 찾아옵니다.
    const targetElement = document.getElementById(targetId);

    // responseBody는 서버가 보낸 응답의 내용물이 담겨있는 '파이프(ReadableStream)'입니다.
    // .getReader()는 이 파이프에서 데이터를 조금씩 꺼내 읽을 수 있는 '수도꼭지' 역할을 하는 reader 객체를 가져옵니다.
    const reader = responseBody.getReader();
    // 서버가 보낸 데이터는 원래 컴퓨터만 알아볼 수 있는 숫자들의 배열(바이트 데이터)입니다.
    // TextDecoder는 이 숫자 배열을 우리가 읽을 수 있는 'utf-8' 형식의 글자(텍스트)로 변환해주는 '번역기'입니다.
    const decoder = new TextDecoder("utf-8");
    let content = "";
    while (true) {
        // '수도꼭지'를 틀어 파이프에서 데이터 한 조각(chunk)이 나올 때까지 기다립니다.
        // 데이터 조각이 나오면 { value, done } 이라는 객체를 반환합니다.
        // value: 읽어온 데이터 조각 (아직은 글자가 아닌 숫자 배열).
        // done: 데이터 파이프가 완전히 비어서 더 이상 읽을 내용이 없으면 true가 되고, 아직 남아있으면 false가 됩니다.
        const { value, done } = await reader.read();

        if (done) break;
        // '번역기'를 사용해 숫자 배열(value)을 우리가 읽을 수 있는 텍스트 조각(chunk)으로 변환합니다.
        let chunk = decoder.decode(value);
        content += chunk;

        // 변환된 텍스트 조각을 출력 대상 엘리먼트에 계속 추가해 나갑니다.
        targetElement.innerHTML = content;

        //채팅 패널의 스크롤을 제일 아래로 내려주는 함수 호출 한다
        springai.scrollToHeight(chatPanelId);

    }
    console.log(content);
    
};

// ##### JSON을 이쁘게 출력하는 함수 #####
springai.printAnswerJson = async function(jsonString, uuid, chatPanelId) {
  const jsonObject = JSON.parse(jsonString);
  // 들여쓰기를 2로 설정해서 이쁘게 문자열로 만듬
  const prettyJson = JSON.stringify(jsonObject, null, 2);
  document.getElementById(uuid).innerHTML = "<pre>" + prettyJson + "</pre>";
  springai.scrollToHeight(chatPanelId);
};

// ##### 진행중임을 표시하는 함수 #####
springai.setSpinner = function(spinnerId, status) {
  if(status) {
    document.getElementById(spinnerId).classList.remove("d-none");
  } else {
    document.getElementById(spinnerId).classList.add("d-none");
  }
}

// ##### 채팅 패널의 스크롤을 제일 아래로 내려주는 함수 #####
springai.scrollToHeight = function (chatPanelId) {
  //DOM 업데이트보다 스크롤 이동이 먼저 되면 안되므로
  //스크롤 이동을 0.1초간 딜레이 시킴
  setTimeout(() => {
    const chatPanelElement = document.getElementById(chatPanelId);
    chatPanelElement.scrollTop = chatPanelElement.scrollHeight;
  }, 100);
};

