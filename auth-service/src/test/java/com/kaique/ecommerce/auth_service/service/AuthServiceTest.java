package com.kaique.ecommerce.auth_service.service;

import com.kaique.ecommerce.auth_service.dtos.AuthResponse;
import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.UserResponse;
import com.kaique.ecommerce.auth_service.dtos.login.LoginRequest;
import com.kaique.ecommerce.auth_service.dtos.refreshToken.RefreshTokenRequest;
import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.*;
import com.kaique.ecommerce.auth_service.messaging.RabbitDomainEventPublisher;
import com.kaique.ecommerce.auth_service.messaging.event.DomainEvent;
import com.kaique.ecommerce.auth_service.repositories.RoleRepository;
import com.kaique.ecommerce.auth_service.repositories.UserRepository;
import com.kaique.ecommerce.auth_service.security.AuthenticatedUser;
import com.kaique.ecommerce.auth_service.security.jwt.JwtProperties;
import com.kaique.ecommerce.auth_service.security.jwt.JwtService;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshToken;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenHashService;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenRepository;
import com.kaique.ecommerce.auth_service.utils.UserCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenHashService refreshTokenHashService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RabbitDomainEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUser_WhenSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("user@email.com", "12345678");

        Role customerRole = new Role(1L, RoleName.CUSTOMER);

        String passwordHash = "hashedPassword";

        User user = User.createCustomer(request.email(), passwordHash, customerRole);

        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse register = authService.register(request);

        verify(roleRepository).findByName(any(RoleName.class));
        verify(passwordEncoder).encode(any(String.class));
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publish(any(DomainEvent.class));

        assertEquals(register.email(), request.email());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExist() throws Exception {
        RegisterRequest request = new RegisterRequest("user@email.com", "12345678");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));

        verify(roleRepository, never()).findByName(any(RoleName.class));
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenRoleNotFound() throws Exception {
        RegisterRequest request = new RegisterRequest("user@email.com", "12345678");

        assertThrows(ResourceNotFoundException.class, () -> authService.register(request));

        verify(roleRepository).findByName(any(RoleName.class));
        verify(passwordEncoder, never()).encode(any(String.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnTokens_WhenSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "12345678");

        AuthenticatedUser authenticatedUser = UserCreator.createAuthenticatedUser();

        JwtUser jwtUser = authenticatedUser.toJwtUser();

        Authentication authentication = mock(Authentication.class);

        String accessToken = "Access-Token";
        String refreshToken = "Refresh-Token";
        String hashedRefreshToken = "hashed-refresh_token";

        User user = UserCreator.createUser();

        RefreshToken refreshTokenToSave = RefreshToken.create(user, hashedRefreshToken, jwtProperties.refreshTokenExpiration());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        when(jwtService.generateAccessToken(jwtUser)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(jwtUser)).thenReturn(refreshToken);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(refreshTokenHashService.hashRefreshToken(refreshToken)).thenReturn(hashedRefreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshTokenToSave);

        AuthResponse response = authService.login(request);

        assertEquals(refreshTokenToSave.getUser(), user);
        assertNotNull(response);

        verify(jwtService).generateAccessToken(jwtUser);
        verify(jwtService).generateRefreshToken(jwtUser);
        verify(userRepository).findByEmail(request.email());
        verify(refreshTokenHashService).hashRefreshToken(refreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreWrong() throws Exception {
        LoginRequest request = new LoginRequest("user@email.com", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtService, never()).generateAccessToken(any(JwtUser.class));
        verify(jwtService, never()).generateRefreshToken(any(JwtUser.class));
        verify(userRepository, never()).findByEmail(request.email());
        verify(refreshTokenHashService, never()).hashRefreshToken(any(String.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void refresh_ShouldReturnNewTokens_WhenSuccessfully() throws Exception {
        User user = UserCreator.createUser();

        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        String hashedRefreshToken = ("hashed-refresh-token");
        RefreshToken entity = RefreshToken.create(user, hashedRefreshToken, Duration.ofDays(30));

        RefreshToken refreshTokenToSave = RefreshToken.create(user, hashedRefreshToken, Duration.ofDays(30));

        JwtUser jwtUser = UserCreator.createJwtUser();

        String refreshToken = "new-refresh-token";
        String accessToken = "new-access-token";

        String newHashedRefreshToken = "new-hashed-refresh-token";

        when(jwtService.validateRefreshToken(request.refreshToken())).thenReturn(jwtUser);
        when(refreshTokenHashService.hashRefreshToken(request.refreshToken())).thenReturn(hashedRefreshToken);
        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken)).thenReturn(Optional.of(entity));
        when(jwtService.generateRefreshToken(any(JwtUser.class))).thenReturn(refreshToken);
        when(jwtService.generateAccessToken(any(JwtUser.class))).thenReturn(accessToken);
        when(refreshTokenHashService.hashRefreshToken(refreshToken)).thenReturn(newHashedRefreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshTokenToSave);

        AuthResponse response = authService.refresh(request);

        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());
    }

    @Test
    void refresh_ShouldThrowException_WhenTokenIsInvalid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

        when(jwtService.validateRefreshToken(request.refreshToken())).thenThrow(InvalidTokenException.class);

        assertThrows(InvalidTokenException.class, () -> authService.refresh(request));

    }

    @Test
    void refresh_ShouldThrowException_WhenUserDisabled() throws Exception {
        User user = UserCreator.createUser();
        user.disableUser(user);

        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        String hashedRefreshToken = ("hashed-refresh-token");

        RefreshToken entity = RefreshToken.create(user, hashedRefreshToken, Duration.ofDays(30));

        JwtUser jwtUser = UserCreator.createJwtUser();

        when(jwtService.validateRefreshToken(request.refreshToken())).thenReturn(jwtUser);
        when(refreshTokenHashService.hashRefreshToken(request.refreshToken())).thenReturn(hashedRefreshToken);
        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken)).thenReturn(Optional.of(entity));

        assertThrows(UnauthorizedException.class, () -> authService.refresh(request));
    }

    @Test
    void refresh_ShouldThrowException_WhenTokenIsRevokedOrExpired() throws Exception {
        User user = UserCreator.createUser();

        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        String hashedRefreshToken = ("hashed-refresh-token");

        RefreshToken entity = RefreshToken.create(user, hashedRefreshToken, Duration.ofDays(30));
        entity.revoke();

        JwtUser jwtUser = UserCreator.createJwtUser();

        when(jwtService.validateRefreshToken(request.refreshToken())).thenReturn(jwtUser);
        when(refreshTokenHashService.hashRefreshToken(request.refreshToken())).thenReturn(hashedRefreshToken);
        when(refreshTokenRepository.findByTokenHash(hashedRefreshToken)).thenReturn(Optional.of(entity));

        assertThrows(InvalidTokenException.class, () -> authService.refresh(request));
    }
}

