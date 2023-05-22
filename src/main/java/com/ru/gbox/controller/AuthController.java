package com.ru.gbox.controller;

import com.ru.gbox.models.SignUpReq;
import com.ru.gbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;


    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody SignUpReq signUpReq) {
        return ResponseEntity.ok(userService.signUp(signUpReq));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(userService.signin(username, password));
    }
}
