package com.kaique.ecommerce.auth_service.repositories;

import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
