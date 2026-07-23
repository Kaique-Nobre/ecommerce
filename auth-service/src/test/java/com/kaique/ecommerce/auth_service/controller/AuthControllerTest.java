package com.kaique.ecommerce.auth_service.controller;

import com.kaique.ecommerce.auth_service.dtos.AuthResponse;
import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.UserResponse;
import com.kaique.ecommerce.auth_service.dtos.login.LoginRequest;
import com.kaique.ecommerce.auth_service.dtos.refreshToken.RefreshTokenRequest;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.BadCredentialsException;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.ConflictException;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.InvalidTokenException;
import com.kaique.ecommerce.auth_service.security.jwt.JwtAuthenticationFilter;
import com.kaique.ecommerce.auth_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_ShouldReturnCreated_WhenSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user@email.com", "12345678");

        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "user@email.com");

        when(authService.register(registerRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_ShouldReturnConflict_WhenEmailAlreadyRegistered() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user@email.com", "12345678");

        when(authService.register(registerRequest)).thenThrow(new ConflictException("Email already registered"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void login_ShouldReturnOK_WhenSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "12345678");

        Set<RoleName> userRoles = new HashSet<>(RoleName.CUSTOMER.ordinal());

        AuthResponse authResponse = new AuthResponse("accessToken", "refreshToken", UUID.randomUUID(), userRoles);

        when(authService.login(request)).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUsersCredentialsIsWrong() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "wrong-password");

        when(authService.login(request)).thenThrow(new BadCredentialsException("Email or password is wrong"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect((jsonPath("$.message").value("Email or password is wrong")));
    }

    @Test
    void refresh_ShouldReturnOk_WhenSuccessfully() throws Exception {
        String refreshTokenValor = "refreshTokenValor";

        Cookie refreshCookie = new Cookie("refreshToken", refreshTokenValor);

        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValor);

        Set<RoleName> userRoles = new HashSet<>(RoleName.CUSTOMER.ordinal());

        AuthResponse authResponse = new AuthResponse("accessToken", "newRefreshToken", UUID.randomUUID(), userRoles);

        when(authService.refresh(request)).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void refresh_ShouldReturnUnauthorized_WhenTokenIsInvalid() throws Exception {
        String refreshTokenValor = "invalid-token";

        Cookie refreshCookie = new Cookie("refreshToken", refreshTokenValor);

        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValor);

        when(authService.refresh(request)).thenThrow(new InvalidTokenException("Invalid or expired token"));

        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }
}
