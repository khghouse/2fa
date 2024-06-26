package com.fa.controller;

import com.fa.request.AuthCodeRequest;
import com.fa.request.LoginRequest;
import com.fa.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(@ModelAttribute LoginRequest loginRequest, Model model) throws Exception {
        String qrImageUrl = loginService.login(loginRequest.getId(), loginRequest.getPassword());
        model.addAttribute("qrImageUrl", qrImageUrl);
        model.addAttribute("id", loginRequest.getId());
        return "loginResult";
    }

    @PostMapping("/auth-code")
    public String authCode(@ModelAttribute AuthCodeRequest authCodeRequest, Model model) {
        model.addAttribute("result", loginService.validationQRCode(authCodeRequest.getId(), authCodeRequest.getAuthCode()));
        return "2faResult";
    }

}
