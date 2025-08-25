package org.engripaye.iamspringbootbackend.repository;


import org.engripaye.iamspringbootbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find by tenantId and email (since you made them unique together)
    Optional<User> findByTenantIdAndEmail(String tenantId, String email);

    // Optional: just by email
    Optional<User> findByEmail(String email);

    // Optional: all users under a tenant
    java.util.List<User> findAllByTenantId(String tenantId);
}
