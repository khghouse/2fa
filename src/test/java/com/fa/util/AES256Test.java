package com.fa.util;

import com.fa.component.AES256;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AES256Test {

    @Test
    @DisplayName("AES256 secret 생성")
    void generateKey() throws Exception {
        // when
        String result = AES256.generateKey();

        // then
        assertThat(Base64.getDecoder().decode(result).length).isEqualTo(32);
    }

    @Test
    @DisplayName("AES256 iv 생성")
    void generateIv() {
        // when
        String result = AES256.generateIV();

        // then
        assertThat(Base64.getDecoder().decode(result).length).isEqualTo(16);
    }

    @Test
    @DisplayName("AES256 클래스를 이용하여 평문을 암호화한다.")
    void encrypt() throws Exception {
        // given
        String plainText = "khghouse@naver.com";

        // when
        String result = AES256.encryptBase64(plainText);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("AES256 클래스를 이용하여 암호화문을 복호화한다.")
    void decrypt() throws Exception {
        // given
        String plainText = "khghouse@naver.com";
        String encrypted = AES256.encryptBase64(plainText);

        // when
        String result = AES256.decrypt(encrypted);

        // then
        assertThat(result).isEqualTo("khghouse@naver.com");
    }

}