package com.hackathon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hackathon.service.FirebaseUserService;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseUserController {

    @Autowired
    private FirebaseUserService firebaseUserService;

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password) {
        return firebaseUserService.createUser(email, password);
    }
}
