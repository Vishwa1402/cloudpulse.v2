package com.cloudpulse.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET =
            "cloudpulsejwtsecretcloudpulsejwtsecret";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(com.cloudpulse.backend.entity.User user) {

        return Jwts.builder()
                .claim("role", user.getRole() != null ? user.getRole().name() : "VIEWER")
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        final Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        return key;
    }

    public boolean isTokenValid(
        String token,
        UserDetails userDetails
) {

    final String username = extractUsername(token);

    return username.equals(userDetails.getUsername());
}
    
}