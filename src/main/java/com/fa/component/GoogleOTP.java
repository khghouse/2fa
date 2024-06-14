package com.fa.component;

import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GoogleOTP {

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();

        // Base32로 인코딩 했을 때, 32바이트 문자열 만들기 위해 20으로 설정
        // 20 * 8 / 5 (Base32 인코딩을 5비트 단위로 함) = 32
        byte[] bytes = new byte[20];

        // bytes 배열을 난수로 채운다.
        random.nextBytes(bytes);

        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

}
