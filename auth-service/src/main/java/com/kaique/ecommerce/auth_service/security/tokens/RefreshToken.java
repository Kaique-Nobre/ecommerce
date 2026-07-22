package com.kaique.ecommerce.auth_service.security.tokens;

import com.kaique.ecommerce.auth_service.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    private OffsetDateTime revokedAt;

    public static RefreshToken create(
            User user,
            String tokenHash,
            Duration expiration
    ) {

        RefreshToken token = new RefreshToken();

        token.id = UUID.randomUUID();
        token.tokenHash = tokenHash;
        token.user = user;
        token.createdAt = OffsetDateTime.now();
        token.expiresAt = OffsetDateTime.now().plus(expiration);
        token.revoked = false;
        token.revokedAt = null;

        return token;
    }

    public void revoke() {

        this.revoked = true;

        this.revokedAt = OffsetDateTime.now();

    }

    public boolean isRevoke() {

        return this.revoked;

    }

    public boolean isExpired() {

        return OffsetDateTime.now()
                .isAfter(expiresAt);

    }
}
