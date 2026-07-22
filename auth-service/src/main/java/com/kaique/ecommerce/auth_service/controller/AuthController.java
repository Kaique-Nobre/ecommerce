package com.kaique.ecommerce.auth_service.controller;

import com.kaique.ecommerce.auth_service.dtos.refreshToken.RefreshTokenRequest;
import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.UserResponse;
import com.kaique.ecommerce.auth_service.dtos.login.LoginRequest;
import com.kaique.ecommerce.auth_service.dtos.AuthResponse;
import com.kaique.ecommerce.auth_service.dtos.login.LoginResponse;
import com.kaique.ecommerce.auth_service.dtos.refreshToken.RefreshTokenResponse;
import com.kaique.ecommerce.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        UserResponse userResponse = authService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", response.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(response.accessToken(), response.userId(), response.userRoles()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        RefreshTokenRequest token = new RefreshTokenRequest(refreshToken);

        AuthResponse response = authService.refresh(token);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", response.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(30))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new RefreshTokenResponse(response.accessToken()));

    }

}
