
## 구글 Authenticator을 이용한 2차 인증

1. 로그인
    1. 로그인이 정상 처리되면 서버에서는 2차 인증을 위한 QR 코드 생성
    2. 해당 유저의 QR 코드 이미지를 응답한다.
2. QR 코드 스캔
    1. Authenticator 앱을 이용하여 QA 코드 스캔
    2. 정상 스캔 되면 "발급자:계정명"으로 6자리 인증 코드 노출
    3. 해당 코드는 30초마다 자동 갱신
3. 2차 인증 코드 입력
    1. 클라이언트로부터 전달받은 인증 코드와 해당 유저의 시크릿을 통해 불러온 OTP 코드를 비교
    2. 코드가 일치하면 2차 인증 처리 완료

<br />

### 참고 자료
- https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
