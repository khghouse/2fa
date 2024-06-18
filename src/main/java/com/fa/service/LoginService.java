package com.fa.service;

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

    @PostConstruct
    public void init() {
        // TODO :: 회원 정보 영속화
        memberMap = new HashMap<>();
        memberMap.put("user1", "1234");
        memberMap.put("user2", "5678");
    }

    public void login(String id, String password) {
        // TODO :: 회원 정보를 조회하고, 전달 받은 id, password 비교
        boolean isMember = Optional.ofNullable(memberMap.get(id))
                .filter(value -> value.equals(password))
                .isPresent();
    }

}
