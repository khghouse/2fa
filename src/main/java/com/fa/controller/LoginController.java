package com.fa.controller;

import com.fa.request.LoginRequest;
import com.fa.service.LoginService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping(value = "/login")
    public String loginProcess(@ModelAttribute LoginRequest loginRequest) throws IOException, WriterException {
        loginService.login(loginRequest.getId(), loginRequest.getPassword());
        return "loginResult";
    }

}
