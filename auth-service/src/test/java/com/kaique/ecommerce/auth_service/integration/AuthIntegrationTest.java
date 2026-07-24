package com.kaique.ecommerce.auth_service.integration;

import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.login.LoginRequest;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.messaging.RabbitDomainEventPublisher;
import com.kaique.ecommerce.auth_service.messaging.RabbitMQConfig;
import com.kaique.ecommerce.auth_service.messaging.event.DomainEvent;
import com.kaique.ecommerce.auth_service.repositories.UserRepository;
import com.kaique.ecommerce.auth_service.security.jwt.JwtService;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshToken;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenHashService;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenRepository;
import com.kaique.ecommerce.auth_service.utils.UserCreator;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenHashService refreshTokenHashService;

    @MockitoBean
    private RabbitDomainEventPublisher eventPublisher;

    @BeforeEach
    void cleanDatabase() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUser_WhenSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("user@email.com", "12345678");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(eventPublisher)
                .publish(any(DomainEvent.class));
    }

    @Test
    void register_ShouldReturnConflict_WhenEmailAlreadyRegistered() throws Exception {
        User user = UserCreator.createUser();
        userRepository.save(user);

        RegisterRequest request = new RegisterRequest("user@email.com", "12345678");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict Error"));
    }

    @Test
    void login_ShouldReturnTokens_WhenSuccessfully() throws Exception {
        String hashedPassword = passwordEncoder.encode("12345678");
        User user = UserCreator.createIntegrationTestUser(hashedPassword);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("user@email.com", "12345678");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreWrong() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "wrong-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Bad Credentials"));
    }

    @Test
    void refresh_ShouldReturnNewAccessTokens_WhenSuccessfully() throws Exception {
        String hashedPassword = passwordEncoder.encode("12345678");
        User user = UserCreator.createIntegrationTestUser(hashedPassword);
        userRepository.save(user);

        JwtUser jwtUser = JwtUser.from(user);

        String refreshToken = jwtService.generateRefreshToken(jwtUser);
        String hashedToken = refreshTokenHashService.hashRefreshToken(refreshToken);

        RefreshToken token = RefreshToken.create(user, hashedToken, Duration.ofDays(30));
        refreshTokenRepository.save(token);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void refreshToken_ShouldReturnUnauthorized_WhenTokenIsInvalid() throws Exception {
        String hashedPassword = passwordEncoder.encode("12345678");
        User user = UserCreator.createIntegrationTestUser(hashedPassword);
        userRepository.save(user);

        Cookie refreshCookie = new Cookie("refreshToken", "invalid-refresh-token");

        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Invalid Token"));
    }

    @Test
    void refresh_ShouldReturnUnauthorized_WhenTokenIsRevoked() throws Exception {
        String hashedPassword = passwordEncoder.encode("12345678");
        User user = UserCreator.createIntegrationTestUser(hashedPassword);
        userRepository.save(user);

        JwtUser jwtUser = JwtUser.from(user);

        String refreshToken = jwtService.generateRefreshToken(jwtUser);
        String hashedToken = refreshTokenHashService.hashRefreshToken(refreshToken);

        RefreshToken token = RefreshToken.create(user, hashedToken, Duration.ofDays(30));
        token.revoke();
        refreshTokenRepository.save(token);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Invalid Token"));
    }
}
