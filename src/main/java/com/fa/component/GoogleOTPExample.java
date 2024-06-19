package com.fa.component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Component
public class GoogleOTPExample {

    /**
     * (최초 1회) GoogleOTP 시크릿 키 생성
     */
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

    /**
     * 6자리 OTP 코드를 불러온다.
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * 시크릿키, 계정명, 발급자를 받아서 구글 OTP 인증용 링크를 생성한다.
     */
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 인증용 바코드 링크를 이용하여 QR코드 이미지를 생성한다.
     */
    public static void createQRCodeImage(String barCode, String account, int height, int width) throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, width, height);

        File file = new File("src/main/resources/static/barcode/" + account + ".png");

        try (FileOutputStream out = new FileOutputStream(file)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

}
