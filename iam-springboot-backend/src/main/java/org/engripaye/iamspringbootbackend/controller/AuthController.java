package org.engripaye.iamspringbootbackend.controller;

import lombok.RequiredArgsConstructor;
import org.engripaye.iamspringbootbackend.config.JwtService;
import org.engripaye.iamspringbootbackend.entities.User;
import org.engripaye.iamspringbootbackend.repository.UserRepository;
import org.engripaye.iamspringbootbackend.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = body.get("password");
        String tenant = TenantContext.get();

        var user = userRepository.findByTenantIdAndEmail(tenant, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");

        var roles = /* load roles for user in this tenant */ List.of("USER");
        String token = service.issue(user.getEmail(), tenant, roles);
        return Map.of("access_token", token, "token_type", "Bearer", "tenant", tenant);
    }

    // AFTER SUCCESSFUL OIDC LOGIN (GOOGLE/GITHUB/AUTH0), Spring Security Stores principal
    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser principal){
        String tenant = TenantContext.get();
        if(principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String email = principal.getEmail();

        // Upsert user in our DB for this tenant
        var user = userRepository.findByTenantIdAndEmail(tenant, email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .tenantId(tenant).email(email).passwordHash("").enabled(true).build()));

        var roles = List.of("USER");
        String token = service.issue(email, tenant, roles);
        return Map.of("email", email, "tenant", tenant, "access_token", token);
    }
}
