package com.moneymanager.service;

import com.moneymanager.config.AuthenticatedUser;
import com.moneymanager.config.JwtService;
import com.moneymanager.dto.auth.AuthResponse;
import com.moneymanager.dto.auth.LoginRequest;
import com.moneymanager.dto.auth.MeResponse;
import com.moneymanager.dto.auth.RegisterRequest;
import com.moneymanager.exception.BadRequestException;
import com.moneymanager.model.Account;
import com.moneymanager.model.User;
import com.moneymanager.repository.AccountRepository;
import com.moneymanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = normalizeEmail(req.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        String hash = passwordEncoder.encode(req.getPassword());
        User u = new User(email, req.getFirstName().trim(), req.getLastName().trim(), hash);
        u = userRepository.save(u);

        // Create a default Cash account (highly useful for demo)
        Account cash = new Account(u.getId(), "Cash", 0.0);
        accountRepository.save(cash);

        MeResponse me = new MeResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());
        String token = jwtService.generateToken(new AuthenticatedUser(u.getId(), u.getEmail()));
        return new AuthResponse(token, me);
    }

    public AuthResponse login(LoginRequest req) {
        String email = normalizeEmail(req.getEmail());
        User u = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        MeResponse me = new MeResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());
        String token = jwtService.generateToken(new AuthenticatedUser(u.getId(), u.getEmail()));
        return new AuthResponse(token, me);
    }

    public MeResponse me(String userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        return new MeResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
