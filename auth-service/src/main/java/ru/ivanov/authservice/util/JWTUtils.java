package ru.ivanov.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ivanov.authservice.dto.UserDto;
import ru.ivanov.authservice.model.RefreshToken;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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

    public RefreshToken generateRefreshToken(UserDto userDto) {
        UUID userId = userDto.id();
        Instant now = Instant.now();
        Instant expirationDate = now.plus(refreshTokenTtl);
        String token = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(UUID.randomUUID().toString())
                .claim("type", "refresh")
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationDate))
                .issuer(issuer)
                .signWith(getSigningKey(refreshTokenSecret))
                .compact();
        return new RefreshToken(userId, token, expirationDate);
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

    public boolean isAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey(accessTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("type").equals("access");
        } catch (ExpiredJwtException e) {
            // Просроченный токен все еще считается access-токеном
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public UUID validateRefreshTokenAndExtractUserId(String token) {
        Claims claims = validateAndParseRefreshToken(token);
        return UUID.fromString(claims.get("userId", String.class));
    }

    public Claims validateAndParseRefreshToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey(refreshTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException("Refresh token expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Unsupported JWT format", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed JWT", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid signature", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Invalid token", e);
        }
    }

    public Claims validateAndParseAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey(accessTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException("Access token expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Unsupported JWT format", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed JWT", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid signature", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Invalid token", e);
        }
    }
}