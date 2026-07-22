package com.kaique.ecommerce.auth_service.security;

import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

    private final UUID id;

    private final String email;

    private final String passwordHash;

    private final boolean enabled;

    private final Set<RoleName> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()

                .map(RoleName::name)

                .map(SimpleGrantedAuthority::new)

                .toList();

    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public JwtUser toJwtUser() {

        return new JwtUser(

                id,

                roles

        );
    }

    public static AuthenticatedUser from(User user) {

        Set<RoleName> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                roles
        );
    }
}
