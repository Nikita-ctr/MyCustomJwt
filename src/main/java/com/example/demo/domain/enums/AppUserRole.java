package com.example.demo.domain.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum AppUserRole implements GrantedAuthority {

    ROLE_ADMIN, ROLE_USER;

    public String getAuthority() {
        return name();
    }

}