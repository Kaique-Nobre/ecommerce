package com.kaique.ecommerce.auth_service.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;

    public String generateAccessToken(JwtUser jwtUser) {
        return generateToken(
                jwtUser,
                TokenType.ACCESS,
                jwtProperties.accessTokenExpiration()
        );
    }

    public String generateRefreshToken(JwtUser jwtUser) {
        return generateToken(
                jwtUser,
                TokenType.REFRESH,
                jwtProperties.refreshTokenExpiration()
        );
    }

    public JwtUser validateAccessToken(String accessToken) {
        return validateToken(accessToken, TokenType.ACCESS);
    }

    public JwtUser validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, TokenType.REFRESH);
    }

    private @NonNull JwtUser validateToken(String refreshToken, TokenType tokenType) {
        try {
            DecodedJWT jwt = verifyToken(refreshToken);

            String type = jwt.getClaim("type").asString();

            if (!type.equals(tokenType.name())) {
                throw new InvalidTokenException("Token is invalid or expired");
            }

            UUID id = UUID.fromString(jwt.getSubject());

            List<String> roles = jwt.getClaim("roles").asList(String.class);

            Set<RoleName> setRoles = roles.stream().map(RoleName::valueOf).collect(Collectors.toSet());

            return new JwtUser(
                    id,
                    setRoles
            );
        } catch (InvalidTokenException | JWTVerificationException exception) {
            throw new InvalidTokenException("Token is invalid or expired");
        }
    }

    private DecodedJWT verifyToken(String token) {
        DecodedJWT jwt = JWT.require(getAlgorithm())
                .withIssuer(jwtProperties.issuer())
                .build().verify(token);
        return jwt;
    }

    private Algorithm getAlgorithm() {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secret());
        return algorithm;
    }

    private String generateToken(JwtUser jwtUser, TokenType tokenType, Duration expiration) {
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(jwtProperties.issuer())
                .withSubject(jwtUser.id().toString())
                .withClaim("type", tokenType.name())
                .withClaim("roles", jwtUser.roles().stream().map(RoleName::name).toList())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(expiration))
                .sign(getAlgorithm());
    }
}
