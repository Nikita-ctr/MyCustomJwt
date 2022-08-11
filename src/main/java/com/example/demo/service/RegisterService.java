package com.example.demo.service;

import com.example.demo.domain.Email;
import com.example.demo.domain.User;
import com.example.demo.domain.VerificationToken;
import com.example.demo.domain.enums.AppUserRole;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.VerificationTokenRepository;
import com.example.demo.service.mail.MailServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
@Transactional
public class RegisterService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailServiceImpl mailService;

    private final UserRepository userRepository;


    public RegisterService(VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, MailServiceImpl mailService, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    public void signup(RegisterRequest registerRequest) {

        if (!userRepository.existsByUsername(registerRequest.getUsername())) {
            User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        user.setAppUserRoles(new ArrayList<>(Arrays.asList(AppUserRole.ROLE_USER)));

        userRepository.save(user);

        String token = generateVerificationToken(user);

        Email email = mailService.generateEmail(user, token);

        mailService.sendMail(email);

    } else
        throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setTokenValue(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    public void verifyAccount(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByTokenValue(token);
        fetchUserAndEnable(verificationToken);
    }

    private void fetchUserAndEnable(VerificationToken verificationToken){
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username);

        user.setEnabled(true);
        userRepository.save(user);
    }
}
