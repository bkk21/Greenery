# ♻️ Greenery: AI를 활용한 분리 배출 방법 안내 앱
- 플레이스토어 다운로드 링크 [[Playstore]](https://play.google.com/store/apps/details?id=com.ert.greenery)
- 2024년도 서울 열린데이터광장 공공데이터 활용 창업경진대회 출품작
- recyclable waste detection App with Kotlin
- yolo v8과 공공 데이터를 활용한 이미지 학습
[[Greenery Recyclable Waste Detection]](https://github.com/the0807/Greenery-Recyclable-Waste-Detection)
- ChatGPT Prompt Engineering을 통해 분리 배출 방법에 관한 연관성 높은 답변 도출
- ChatGPT Fine-Tuning(RAG)를 통해 성능 향상 (추후 관련 레포지토리 공개 예정)
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
|![SC 1](https://github.com/bkk21/Greenery/assets/108513540/6566fb98-cf05-49c6-ae8e-3f06aadb4cb7)|![SC 2](https://github.com/bkk21/Greenery/assets/108513540/03a3eeb6-3c29-43ad-8568-f5ec38b3a3f3)|![SC 2](https://github.com/bkk21/Greenery/assets/108513540/45cc4b79-9bd3-4ba8-ab01-c505109d0a19)|![SC 4](https://github.com/bkk21/Greenery/assets/108513540/200d4252-a56a-4fd0-ad32-67c499a066d8)
|:---:|:---:|:---:|:---:|
| <center>메인 화면</center> | <center>채팅 예시</center> | <center>채팅 중 내 주변 쓰레기통 정보</center>| <center>지도에서 보는 주변 쓰레기통 정보</center>|

|![SC 5](https://github.com/bkk21/Greenery/assets/108513540/daae3504-d22a-45f3-b7ac-4a4a05d1e813)|![SC 6](https://github.com/bkk21/Greenery/assets/108513540/539f05cd-37be-4a4d-bbd1-5b4d1e0d5b82)|![SC 7](https://github.com/bkk21/Greenery/assets/108513540/e98a6d82-b002-45d2-a237-d0e0dd9990e5)|![SC 8](https://github.com/bkk21/Greenery/assets/108513540/2a6d28dd-b111-4bf2-a7c2-3a0d6cde3755)|
|:---:|:---:|:---:|:---:|
| <center>사진으로 보는 방법 확인</center> | <center>내 주변 500M 안의 쓰레기통 지도</center> | <center>특정 위치의 상세 정보 확인</center>| <center>분리배출 TIP</center>|
