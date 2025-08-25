package org.engripaye.iamspringbootbackend.rbac;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long permissionId;
    String tenantId;
}
