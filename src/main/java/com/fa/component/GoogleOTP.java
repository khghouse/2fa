package com.fa.component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class GoogleOTP {

    private final String ISSUER = "study2FA";

    /**
     * 시크릿키, 계정명, 발급자를 받아서 구글 OTP 인증용 링크를 생성한다.
     */
    public String getGoogleAuthenticatorBarCode(String account, String secretKey) {
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
    public String createQRCodeImage(String barCode, String account) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, 200, 200);
        String fileName = AES256.encryptBase32(account) + ".png";
        File file = new File("src/main/resources/static/image/qr/" + fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }

        return fileName;
    }

    /**
     * 클라이언트로부터 전달받은 authCode와 시크릿키를 통해 조회한 OTP 코드를 비교한다.
     */
    public boolean validationOTPCode(String authCode, String secretKey) {
        return authCode.equals(getTOTPCode(secretKey)) ? true : false;
    }

    /**
     * 6자리 OTP 코드를 불러온다.
     */
    private String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

}
