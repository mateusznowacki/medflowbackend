package pl.medflow.medflowbackend.domain.auth;

import org.springframework.http.ResponseCookie;

public record LoginResult(LoginResponse body, ResponseCookie refreshCookie) {}
