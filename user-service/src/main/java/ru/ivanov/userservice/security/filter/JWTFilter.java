package ru.ivanov.userservice.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ivanov.userservice.security.UserDetailsImpl;
import ru.ivanov.userservice.utils.JWTUtils;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromAuthHeader(request);
        if (token != null) {
            try {
                Authentication auth = authenticateToken(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                handleAuthenticationError(response, "Invalid JWT token: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

        private String extractTokenFromAuthHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    private Authentication authenticateToken(String token) {
        if (jwtUtils.isServiceToken(token)) {
            return handleServiceToken(token);
        } else if (jwtUtils.isAccessToken(token)) {
            return handleAccessToken(token);
        }
        throw new JwtException("Unsupported JWT type");
    }

    private Authentication handleServiceToken(String serviceToken) {
        Claims claims = jwtUtils.validateAndParseServiceToken(serviceToken);
        return createServiceAuthentication(claims);
    }

    private Authentication handleAccessToken(String accessToken) {
        Claims claims = jwtUtils.validateAndParseAccessToken(accessToken);
        return createUsernamePasswordAuthentication(claims);
    }

    @SuppressWarnings("unchecked")
    private Authentication createServiceAuthentication(Claims claims) {
        String issuer = claims.getIssuer();
        UUID userId = null;
        Object userIdClaim = claims.get("userId");

        if (userIdClaim != null) {
            userId = UUID.fromString(userIdClaim.toString());
        }

        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetailsImpl userDetails = new UserDetailsImpl(userId, issuer, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @SuppressWarnings("unchecked")
    private Authentication createUsernamePasswordAuthentication(Claims claims) {
        UUID userId = UUID.fromString(claims.getSubject());
        String username = claims.get("username", String.class);

        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails userDetails = new UserDetailsImpl(userId, username, authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}