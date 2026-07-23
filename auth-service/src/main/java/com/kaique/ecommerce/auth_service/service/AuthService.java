package com.kaique.ecommerce.auth_service.service;

import com.kaique.ecommerce.auth_service.dtos.refreshToken.RefreshTokenRequest;
import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.UserResponse;
import com.kaique.ecommerce.auth_service.dtos.login.LoginRequest;
import com.kaique.ecommerce.auth_service.dtos.AuthResponse;
import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.*;
import com.kaique.ecommerce.auth_service.repositories.RoleRepository;
import com.kaique.ecommerce.auth_service.repositories.UserRepository;
import com.kaique.ecommerce.auth_service.security.AuthenticatedUser;
import com.kaique.ecommerce.auth_service.security.jwt.JwtProperties;
import com.kaique.ecommerce.auth_service.security.jwt.JwtService;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshToken;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenHashService;
import com.kaique.ecommerce.auth_service.security.tokens.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenHashService refreshTokenHashService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Role 'CUSTOMER' was not found"));

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.createCustomer(
                request.email(),
                passwordHash,
                customerRole
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authenticate.getPrincipal();

            JwtUser jwtUser = authenticatedUser.toJwtUser();

            String accessToken = jwtService.generateAccessToken(jwtUser);
            String refreshToken = jwtService.generateRefreshToken(jwtUser);

            Optional<User> user = userRepository.findByEmail(request.email());

            String hashedRefreshToken = refreshTokenHashService.hashRefreshToken(refreshToken);

            RefreshToken refreshTokenToSave = RefreshToken.create(user.get(), hashedRefreshToken, Duration.ofDays(jwtProperties.refreshTokenExpiration().toDays()));

            refreshTokenRepository.save(refreshTokenToSave);

            return new AuthResponse(accessToken, refreshToken, jwtUser.id(), jwtUser.roles());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Email or Password is wrong");
        }
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        jwtService.validateRefreshToken(refreshToken);

        String hashedToken = refreshTokenHashService.hashRefreshToken(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

        User user = entity.getUser();

        if(!user.isEnabled()) {
            throw new UnauthorizedException("This user is not authorized");
        }

        if(entity.isExpired() || entity.isRevoke()) {
            throw new InvalidTokenException("Token is revoked or expired token");
        }

        entity.revoke();

        refreshTokenRepository.save(entity);

        JwtUser jwtUser = JwtUser.from(user);

        String accessToken = jwtService.generateAccessToken(jwtUser);
        String newRefreshToken = jwtService.generateRefreshToken(jwtUser);

        String newHash = refreshTokenHashService.hashRefreshToken(newRefreshToken);

        RefreshToken refreshTokenToSave = RefreshToken.create(user, newHash, Duration.ofDays(jwtProperties.refreshTokenExpiration().toDays()));

        refreshTokenRepository.save(refreshTokenToSave);

        return new AuthResponse(accessToken, newRefreshToken, user.getId(), jwtUser.roles());
    }
}
