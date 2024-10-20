package com.hackathon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hackathon.service.FirebaseUserService;

@RestController
@RequestMapping("/firebase")
public class FirebaseUserController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password) {
        return firebaseUserService.createUser(email, password);
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password) {
        return firebaseUserService.loginUser(email, password);
    }
}
