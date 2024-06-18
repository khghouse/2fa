package com.fa.component;

import com.google.zxing.WriterException;
import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GoogleOTPTest {

    @Value("${google.otp.secret}")
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
    @Disabled
    @DisplayName("6자리 코드 리턴한다.")
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

    @Test
    @DisplayName("시크릿키, 계정명, 발급자를 매개변수로 전달하여 구글 OTP 인증용 링크를 생성한다.")
    void getGoogleAuthenticatorBarCode() {
        // given
        String account = "khghouse@naver.com";
        String issuer = "study2FA";

        // when
        String result = GoogleOTP.getGoogleAuthenticatorBarCode(secret, account, issuer);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("구글 OTP 바코드 데이터로 QR코드 이미지를 생성한다.")
    void createQRCodeImage() throws IOException, WriterException {
        // given
        String account = "khghouse@naver.com";
        String issuer = "study2FA";
        String barCode = GoogleOTP.getGoogleAuthenticatorBarCode(secret, account, issuer);

        // when
        GoogleOTP.createQRCodeImage(barCode, account, 200, 200);

        // then
        File result = new File("src/main/resources/static/barcode/" + account + ".png");
        assertThat(result.exists()).isTrue();
    }

}