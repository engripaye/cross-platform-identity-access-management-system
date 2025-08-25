package org.engripaye.iamspringbootbackend.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final String issuer;
    private final Duration expires;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expires}") Duration expires
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long.");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer must not be empty.");
        }
        this.secret = secret;
        this.issuer = issuer;
        this.expires = expires != null ? expires : Duration.ofMinutes(60); // default 60m
    }

    public String issue(String subject, String tenantId, Collection<String> roles) {
        var now = new Date();
        var exp = Date.from(now.toInstant().plus(expires));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .issueTime(now)
                .expirationTime(exp)
                .claim("tenant", tenantId)
                .claim("roles", roles)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        try {
            signedJWT.sign(new MACSigner(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JWT", e);
        }

        return signedJWT.serialize();
    }

}
