package com.fa.service;

import com.fa.component.GoogleOTP;
import com.fa.response.Member;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private Map<String, Member> memberMap;
    private final GoogleOTP googleOTP;

    @PostConstruct
    public void init() {
        // TODO :: 회원 정보 영속화
        Member member1 = Member.builder()
                .id("user1")
                .password("1234")
                .secret("KQGTAEDEGG7F7OHRBILN4B2VK2SPCFJ7")
                .build();

        Member member2 = Member.builder()
                .id("user2")
                .password("5678")
                .secret("DFDQJK6WWPUZ73XILK5PBCJ5TWMDL3KI")
                .build();

        memberMap = new HashMap<>();
        memberMap.put("user1", member1);
        memberMap.put("user2", member2);
    }

    public String login(String id, String password) throws Exception {
        // TODO :: 회원 정보를 조회하고, 전달 받은 id, password 비교
        Optional<Member> optionalMember = Optional.ofNullable(memberMap.get(id))
                .filter(member -> member.getPassword().equals(password));

        if (!optionalMember.isPresent()) {
            return null; // qrImageUrl
        }

        Member member = optionalMember.get();
        String barCode = googleOTP.getGoogleAuthenticatorBarCode(member.getId(), member.getSecret());
        String fileName = googleOTP.createQRCodeImage(barCode, member.getId());
        return "/images/" + fileName; // qrImageUrl
    }

    public boolean validationQRCode(String id, String authCode) {
        Member member = memberMap.get(id);
        return googleOTP.validationOTPCode(authCode, member.getSecret());
    }

}
