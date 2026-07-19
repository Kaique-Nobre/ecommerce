package com.kaique.ecommerce.auth_service.service;

import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.InvalidTokenException;
import com.kaique.ecommerce.auth_service.security.jwt.JwtProperties;
import com.kaique.ecommerce.auth_service.security.jwt.JwtService;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(
                "secret-key",
                Duration.ofMinutes(15),
                Duration.ofDays(30),
                "auth-service"
        );

        jwtService = new JwtService(jwtProperties);
    }


    @Test
    void generateAccessToken_ShouldGenerateAccessToken_WhenSuccessfully() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String accessToken = jwtService.generateAccessToken(jwtUser);

        assertNotNull(accessToken);
    }

    @Test
    void generateRefreshToken_ShouldGenerateRefreshToken_WhenSuccessfully() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String refreshToken = jwtService.generateRefreshToken(jwtUser);

        assertNotNull(refreshToken);
    }

    @Test
    void validateAccessToken_ShouldValidateAccessToken_WhenSuccessfully() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String accessToken = jwtService.generateAccessToken(jwtUser);

        JwtUser AuthenticatedUser = jwtService.validateAccessToken(accessToken);

        assertNotNull(AuthenticatedUser);
        assertEquals(jwtUser, AuthenticatedUser);
    }

    @Test
    void validateAccessToken_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = new String("invalid-access-token");

        assertThrows(InvalidTokenException.class, () -> jwtService.validateAccessToken(invalidToken));
    }

    @Test
    void validateAccessToken_ShouldThrowException_WhenTypeTokenIsInvalid() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String accessToken = jwtService.generateRefreshToken(jwtUser);

        assertThrows(InvalidTokenException.class, () -> jwtService.validateAccessToken(accessToken));
    }

    @Test
    void validateRefreshToken_ShouldValidateRefreshToken_WhenSuccessfully() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String accessToken = jwtService.generateRefreshToken(jwtUser);

        JwtUser AuthenticatedUser = jwtService.validateRefreshToken(accessToken);

        assertNotNull(AuthenticatedUser);
        assertEquals(jwtUser, AuthenticatedUser);
    }

    @Test
    void validateRefreshToken_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = new String("invalid-refresh-token");

        assertThrows(InvalidTokenException.class, () -> jwtService.validateRefreshToken(invalidToken));
    }

    @Test
    void validateRefreshToken_ShouldThrowException_WhenTypeTokenIsInvalid() {
        JwtUser jwtUser = new JwtUser(UUID.randomUUID(), new HashSet<RoleName>(RoleName.CUSTOMER.ordinal()));

        String accessToken = jwtService.generateAccessToken(jwtUser);

        assertThrows(InvalidTokenException.class, () -> jwtService.validateRefreshToken(accessToken));
    }

}
