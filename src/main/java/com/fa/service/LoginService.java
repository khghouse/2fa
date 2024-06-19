package com.fa.service;

import com.fa.component.AES256;
import com.fa.component.GoogleOTP;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private Map<String, String> memberMap;
    private final GoogleOTP googleOTP;

    @PostConstruct
    public void init() {
        // TODO :: 회원 정보 영속화
        memberMap = new HashMap<>();
        memberMap.put("user1", "1234");
        memberMap.put("user2", "5678");
    }

    public String login(String id, String password) throws Exception {
        // TODO :: 회원 정보를 조회하고, 전달 받은 id, password 비교
        boolean isMember = Optional.ofNullable(memberMap.get(id))
                .filter(value -> value.equals(password))
                .isPresent();

        if (isMember) {
            String barCode = googleOTP.getGoogleAuthenticatorBarCode(id);
            googleOTP.createQRCodeImage(barCode, id);
        }

        return AES256.encrypt(id); // qrImageFileName
    }

}
