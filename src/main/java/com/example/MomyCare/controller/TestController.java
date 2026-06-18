package com.example.MomyCare.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final PasswordEncoder encoder;

    public TestController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }


    @GetMapping("/password")
    public String testPassword(){
        return encoder.encode("123456");
    }
}