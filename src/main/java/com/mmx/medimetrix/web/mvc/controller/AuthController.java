package com.mmx.medimetrix.web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @GetMapping("/app/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/app/login")
    public String doLogin() {
        return "redirect:/app";
    }
}