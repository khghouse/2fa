package com.fa.component;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GoogleOTPTest {

    @Value("${secret}")
    private String secret;

    /**
     * 1. 바이너리 표현
     * 72 (십진수) = 01001000 (이진수)
     * <p>
     * 2. 5비트 단위로 나누기
     * Base32 인코딩은 데이터를 5비트 단위로 쪼개어 인코딩
     * -> 01001 00000
     * <p>
     * 3. 해당하는 Base32 문자로 변환
     * Base64 인코딩 테이블
     * -> ABCDEFGHIJKLMNOPQRSTUVWXYZ234567
     * <p>
     * 01001 -> 9 (십진수) -> 'J'
     * 00000 -> 0 (십진수) -> 'A'
     * <p>
     * 4. 패딩 추가
     * Base32 인코딩 문자열의 길이는 항상 8의 배수여야 한다. 빈 공백은 '='로 채워진다.
     * JA -> JA======
     */
    @Test
    @DisplayName("Base32로 인코딩 되는 과정 확인")
    void base32Test() {
        // given
        byte[] bytes = new byte[]{72};
        Base32 base32 = new Base32();

        // when
        String result = base32.encodeToString(bytes);

        // then
        assertThat(result).isEqualTo("JA======");
    }

    @Test
    @DisplayName("Base32로 인코딩된 secret을 생성한다.")
    void generateSecretKey() {
        // when
        String result = GoogleOTP.generateSecretKey();
        System.out.println("secret : " + result);

        // then
        assertThat(result.getBytes().length).isEqualTo(32);
    }

    @Test
    @DisplayName("6자리 코드 리턴")
    void getTOTPCode() {
        String lastCode = null;
        while (true) {
            String code = GoogleOTP.getTOTPCode(secret);
            if (!code.equals(lastCode)) {
                System.out.println(code);
            }
            lastCode = code;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

}