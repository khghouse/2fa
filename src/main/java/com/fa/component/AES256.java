package com.fa.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AES256 {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;
    private static SecretKey key;
    private static byte[] iv;

    @Value("${aes256.key}")
    private void setKey(String key) {
        byte[] keyBytes = base64ToBytes(key);
        AES256.key = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    @Value("${aes256.iv}")
    private void setIv(String iv) {
        AES256.iv = base64ToBytes(iv);
    }

    /**
     * 키 생성
     */
    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM); // AES 알고리즘에 대한 KeyGenerator 객체 생성
        keyGenerator.init(KEY_SIZE); // 256비트 키 사이즈로 초기화
        SecretKey secretKey = keyGenerator.generateKey(); //  키 생성
        return bytesToBase64(secretKey.getEncoded());
    }

    /**
     * IV 생성
     */
    public static String generateIV() {
        byte[] iv = new byte[IV_SIZE]; // IV를 저장할 바이트 배열 생성 (16바이트)
        new SecureRandom().nextBytes(iv); // SecureRandom을 사용하여 IV에 랜덤 값 채우기
        return bytesToBase64(iv); // 생성된 IV 반환
    }

    /**
     * 암호화
     */
    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION); // AES/CBC/PKCS5Padding 알고리즘을 사용하는 Cipher 객체 생성
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); // IV를 포함하는 IvParameterSpec 객체 생성
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec); // Cipher 객체를 암호화 모드로 초기화

        byte[] cipherText = cipher.doFinal(plainText.getBytes()); // 평문을 암호화하여 바이트 배열로 반환
        byte[] cipherTextWithIv = new byte[iv.length + cipherText.length]; // IV와 암호문을 포함할 바이트 배열 생성
        System.arraycopy(iv, 0, cipherTextWithIv, 0, iv.length); // IV를 바이트 배열의 앞 부분에 복사
        System.arraycopy(cipherText, 0, cipherTextWithIv, iv.length, cipherText.length); // 암호문을 그 뒤에 복사

        return bytesToBase64(cipherTextWithIv); // IV와 암호문을 포함한 바이트 배열을 Base64 인코딩하여 문자열로 반환
    }

    /**
     * 복호화
     */
    public static String decrypt(String encryptedText) throws Exception {
        byte[] decodedText = base64ToBytes(encryptedText); // Base64 인코딩된 암호문을 디코딩하여 바이트 배열로 변환

        byte[] iv = new byte[IV_SIZE]; // IV를 저장할 바이트 배열 생성
        System.arraycopy(decodedText, 0, iv, 0, iv.length); // 디코딩된 바이트 배열에서 IV를 복사

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); // IV를 포함하는 IvParameterSpec 객체 생성

        byte[] cipherText = new byte[decodedText.length - iv.length]; // 암호문을 저장할 바이트 배열 생성
        System.arraycopy(decodedText, iv.length, cipherText, 0, cipherText.length); // 디코딩된 바이트 배열에서 IV 이후의 암호문을 복사

        Cipher cipher = Cipher.getInstance(TRANSFORMATION); // AES/CBC/PKCS5Padding 알고리즘을 사용하는 Cipher 객체 생성
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec); // Cipher 객체를 복호화 모드로 초기화

        byte[] plainText = cipher.doFinal(cipherText); // 암호문을 복호화하여 평문 바이트 배열로 반환

        return new String(plainText); // 평문 바이트 배열을 문자열로 변환하여 반환
    }

    /**
     * bate[] -> String (Base64)
     */
    private static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * String (Base64) -> byte[]
     */
    private static byte[] base64ToBytes(String text) {
        return Base64.getDecoder().decode(text);
    }

}
