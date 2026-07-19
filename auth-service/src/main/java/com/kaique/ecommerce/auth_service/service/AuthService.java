package com.kaique.ecommerce.auth_service.service;

import com.kaique.ecommerce.auth_service.dtos.RegisterRequest;
import com.kaique.ecommerce.auth_service.dtos.UserResponse;
import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.ConflictException;
import com.kaique.ecommerce.auth_service.exceptions.genericExceptions.ResourceNotFoundException;
import com.kaique.ecommerce.auth_service.repositories.RoleRepository;
import com.kaique.ecommerce.auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
}
