package ru.ivanov.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.PublicJwk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ivanov.authservice.dto.UserDto;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtils {
    @Value("${jwt.access.secret}")
    String accessTokenSecret;

    @Value("${jwt.access.ttl}")
    Duration accessTokenTtl;

    @Value("${jwt.refresh.secret}")
    String refreshTokenSecret;

    @Value("${jwt.refresh.ttl}")
    Duration refreshTokenTtl;

    @Value("${jwt.service.secret}")
    String serviceTokenSecret;

    @Value("${jwt.service.ttl}")
    Duration serviceTokenTtl;

    @Value("${jwt.issuer}")
    private String issuer;


    public String generateAccessToken(UserDto userDto) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(UUID.randomUUID().toString())
                .claim("type", "access")
                .subject(userDto.id().toString())
                .claim("username", userDto.username())
                .claim("roles", userDto.roles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .issuer(issuer)
                .signWith(getSigningKey(accessTokenSecret))
                .compact();
    }

    public String generateRefreshToken(UserDto userDto) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .subject(userDto.id().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshTokenTtl)))
                .issuer(issuer)
                .signWith(getSigningKey(refreshTokenSecret))
                .compact();
    }

    public String generateServiceToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(UUID.randomUUID().toString())
                .claim("type", "service")
                .claim("userId", userId)
                .claim("roles", List.of("ROLE_AUTH_SERVICE"))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(serviceTokenTtl)))
                .issuer(issuer)
                .signWith(getSigningKey(serviceTokenSecret))
                .compact();
    }






    private SecretKey getSigningKey(String tokenSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
    }







    public UUID extractUserIdFromRefreshToken(String token) {
        return UUID.fromString(extractAllClaimsFromRefreshToken(token).getSubject());
    }

    private Claims extractAllClaimsFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(refreshTokenSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    private java.util.Date extractExpirationFromToken(String token) {
//        return extractAllClaimsFromToken(token).getExpiration();
//    }

//    public String extractUsernameFromToken(String token) {
//        return extractAllClaimsFromToken(token).get("username", String.class);
//    }






//    private Claims extractAllClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(getAccessTokenSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }

//    public boolean validateToken(String token) {
//        return extractExpirationFromToken(token).after(new java.util.Date());
//    }
}