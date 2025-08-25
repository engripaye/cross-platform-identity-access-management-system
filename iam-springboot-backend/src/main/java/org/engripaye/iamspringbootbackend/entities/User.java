package org.engripaye.iamspringbootbackend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", indexes=@Index(name = "idx_users_tenant_email", columnList = "tenantId,email", unique = true))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String tenantId;
    String email;
    String passwordHash;
    boolean enabled = true;
}
