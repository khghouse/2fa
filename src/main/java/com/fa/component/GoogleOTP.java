package com.fa.component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class GoogleOTP {

    @Value("${google.otp.secret}")
    private String secretKey;

    private final String ISSUER = "study2FA";

    /**
     * 시크릿키, 계정명, 발급자를 받아서 구글 OTP 인증용 링크를 생성한다.
     */
    public String getGoogleAuthenticatorBarCode(String account) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(ISSUER + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(ISSUER, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 인증용 바코드 링크를 이용하여 QR코드 이미지를 생성한다.
     */
    public void createQRCodeImage(String barCode, String account) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, 200, 200);

        File file = new File("src/main/resources/static/image/qr/" + AES256.encrypt(account) + ".png");

        try (FileOutputStream out = new FileOutputStream(file)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

}
