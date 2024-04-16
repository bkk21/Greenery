# ♻️ Greenery : AI를 이용한 재활용품 분리 배출 안내 앱
- 2024년도 빅데이터 활용 미래 사회문제 해결 아이디어 해커톤 출품작
- recyclable waste detection App with Kotlin
- yolo v8과 공공 데이터를 활용한 이미지 학습
[Greenery Recyclable Waste Detection](https://github.com/the0807/Greenery-Recyclable-Waste-Detection)
- ChatGPT Prompt Engineering을 통해 분리 배출 방법에 관한 연관성 높은 답변 도출
- ChatGPT Fine-Tuning(RAG)를 통해 성능 향상 (진행 중 - 추후 관련 레포지토리 생성 예정)
<br><br>

# 🏢 Position
- Design
- Front-end (APP)
- Prompt Engineering
- collaborators
  - [Taehyun](https://github.com/the0807)
  - [Dohwan](https://github.com/ehghks021203)
<br><br>

# 📱 App
- figma
- Android Studio
- Kotlin
- Retrofit2 
  - 해당 부분은 서버 주소 문제로 인해 커밋하지 않았습니다.
  - 자료가 필요하신 분은 Velog를 참조해주시길 바랍니다. [✨ kk21's Velog Link](https://velog.io/@kk21/posts)
- flaticon
  - <a href="https://www.flaticon.com/kr/free-icons/" title="새싹 아이콘">새싹 아이콘  제작자: Nikita Golubev - Flaticon</a>
<br><br>

# 💬 ChatBot - Prompt Engineering / Fine-Tuning
- 챗봇의 경우 서버에서 작업인 진행되어 해당 부분의 코드는 다른 레포지토리에 커밋 될 예정입니다.
- Fine-Tuning
  - RAG를 활용하여 Fine-Tuning 진행
  - 할루시네이션 해결<br>
- Prompt 일부
  - 역할 부여 및 가이드 라인 제공
```
너는 지금부터 환경 운동가로 나랑 대화를 할거야. 나는 어떤 물건에 대해 분리 수거 방법에 대해 질문을 할거고 너는 분리 수거 방법을 알려주면 돼.
질문의 형태는 '통조림캔의 배출 방법에 대해 알려줘'와 같이 할거고, 너는 이에 대한 대답을 하면 되는데 첫 문장은 '통조림캔의 배출 방법에 대해 알려드리겠습니다.'의 구조로 시작해야 해 

···(생략)

자 그럼 이제 대부터 대화를 시작해보자.

$data_text 의 배출 방법에 대해 알려줘
```
<br><br>

# 🌱 Result
![app1](https://github.com/bkk21/Greenery/assets/108513540/26191155-f574-4e3b-8bc2-fc6109c69070)|![app2](https://github.com/bkk21/Greenery/assets/108513540/89284dae-61b6-4b0b-baf4-e77a37dc2326)|![app3](https://github.com/bkk21/Greenery/assets/108513540/427ff9b2-640b-41ee-aa99-c7caac3e7935)
|:---:|:---:|:---:|
| <center>메인 화면</center> | <center>카메라 인식 화면</center> | <center>카메라 인식 결과 화면</center>|

![app4](https://github.com/bkk21/Greenery/assets/108513540/fcc3321b-889c-4666-b092-4b7ef2adaaa8)|![app5](https://github.com/bkk21/Greenery/assets/108513540/12d35c98-b02e-465e-b1ee-19968b8e653b)
|:---:|:---:|
|<center>챗봇 화면1</center>|<center>챗봇 화면2</center>
