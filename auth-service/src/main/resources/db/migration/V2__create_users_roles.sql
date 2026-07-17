CREATE TABLE users
    (
        id UUID PRIMARY KEY,
        email VARCHAR(255) NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        enabled BOOLEAN NOT NULL DEFAULT TRUE,
        created_at TIMESTAMPTZ NOT NULL,
        updated_at TIMESTAMPTZ NOT NULL,
        CONSTRAINT  uk_users_email UNIQUE(email)
    );

CREATE TABLE roles
    (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(50) NOT NULL,
        CONSTRAINT uk_roles_name UNIQUE(name)
    );

CREATE TABLE user_roles
    (
        user_id UUID NOT NULL,
        role_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT  fk_user_roles_user
            FOREIGN KEY(user_id)
                REFERENCES users(id)
                ON DELETE CASCADE,

        CONSTRAINT  fk_user_roles_role
            FOREIGN KEY(role_id)
                REFERENCES roles(id)
    );