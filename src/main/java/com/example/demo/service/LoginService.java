package com.example.demo.service;

import com.example.demo.domain.RefreshToken;
import com.example.demo.domain.User;
import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService {

    private final JwtTokenProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public LoginService(JwtTokenProvider jwtProvider, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public Map<String, String> login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user.isEnabled()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            String refresh = createRefreshToken(user.getId());
            String access = createAccessToken(loginRequest.getUsername());
            return Map.of("access token:", access, "refresh token", refresh);
        } else
            throw new CustomException("User is not enabled", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String createAccessToken(String username) {
        return jwtProvider.createToken(username, userRepository.findByUsername(username).getAppUserRoles());
    }

    private String createRefreshToken(Long id) {
        return refreshTokenService.createRefreshToken(id).getToken();
    }

    public String refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = createAccessToken(user.getUsername());
                    return accessToken;
                })
                .orElseThrow(() -> new RuntimeException(requestRefreshToken + "Refresh token is not in database!"));
    }
}
