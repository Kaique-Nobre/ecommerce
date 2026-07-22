package com.kaique.ecommerce.auth_service.security.jwt;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank
        String secret,

        Duration accessTokenExpiration,

        Duration refreshTokenExpiration,

        @NotBlank
        String issuer
) {

}
