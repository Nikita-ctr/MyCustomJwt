package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedResource {

    @GetMapping("/get")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getProtected() {
        return "Secret";
    }
}
