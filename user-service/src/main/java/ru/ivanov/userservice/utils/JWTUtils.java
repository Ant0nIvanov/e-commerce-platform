package ru.ivanov.userservice.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTUtils {

    @Value("${jwt.access.secret}")
    private String accessTokenSecret;

    @Value("${jwt.service.secret}")
    private String serviceTokenSecret;

    public boolean isServiceToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey(serviceTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("type").equals("service");
        } catch (ExpiredJwtException e) {
            return true; // Просроченный токен все еще сервисный
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims validateAndParseServiceToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey(serviceTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims;
        } catch (ExpiredJwtException e) {
            throw new JwtException("Service token expired", e);
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

    public Claims validateAndParseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey(accessTokenSecret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Дополнительная проверка обязательных claims
            if (claims.getSubject() == null ||
                claims.get("username") == null ||
                claims.get("roles") == null) {
                throw new MalformedJwtException("Missing required claims in access token");
            }

            return claims;
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

    private SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}