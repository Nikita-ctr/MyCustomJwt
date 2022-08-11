package com.example.demo.controller;

import com.example.demo.domain.RefreshToken;
import com.example.demo.dto.LogOutRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.service.LoginService;
import com.example.demo.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;

    private final RefreshTokenService refreshTokenService;

    public LoginController(LoginService loginService, RefreshTokenService refreshTokenService) {
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest loginRequest) {
       return ResponseEntity.status(HttpStatus.OK).body(loginService.login(loginRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@Valid @RequestBody TokenRefreshRequest request){
        String requestRefreshToken = request.getRefreshToken();
        return ResponseEntity.status(HttpStatus.OK).body(loginService.refreshToken(requestRefreshToken));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        return ResponseEntity.ok().body("Success logout!");
    }
}
