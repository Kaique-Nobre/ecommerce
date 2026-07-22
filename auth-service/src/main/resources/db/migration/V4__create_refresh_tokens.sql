CREATE TABLE refresh_tokens
    (
        id UUID PRIMARY KEY,
        token_hash VARCHAR(255) NOT NULL UNIQUE,
        user_id UUID NOT NULL,
        created_at TIMESTAMPTZ NOT NULL,
        expires_at TIMESTAMPTZ NOT NULL,
        revoked BOOLEAN NOT NULL DEFAULT FALSE,
        revoked_at TIMESTAMPTZ,
        CONSTRAINT  fk_refresh_tokens_user
            FOREIGN KEY (user_id)
                REFERENCES users(id)
    );