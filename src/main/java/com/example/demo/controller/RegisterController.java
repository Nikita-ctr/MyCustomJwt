package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService){
        this.registerService = registerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        registerService.signup(registerRequest);
        return new ResponseEntity<>("User registration successful", HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
       registerService.verifyAccount(token);
       return new ResponseEntity<>("Account activated successfully",HttpStatus.OK);
    }
}
