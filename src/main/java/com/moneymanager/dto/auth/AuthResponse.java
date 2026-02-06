package com.moneymanager.dto.auth;

public class AuthResponse {
    private String token;
    private MeResponse user;

    public AuthResponse() {}

    public AuthResponse(String token, MeResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MeResponse getUser() {
        return user;
    }

    public void setUser(MeResponse user) {
        this.user = user;
    }
}
