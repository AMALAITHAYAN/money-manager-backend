package com.moneymanager.controller;

import com.moneymanager.dto.auth.AuthResponse;
import com.moneymanager.dto.auth.LoginRequest;
import com.moneymanager.dto.auth.MeResponse;
import com.moneymanager.dto.auth.RegisterRequest;
import com.moneymanager.service.AuthService;
import com.moneymanager.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/me")
    public MeResponse me() {
        String userId = SecurityUtil.requireUserId();
        return authService.me(userId);
    }
}
