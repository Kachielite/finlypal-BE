package com.derrick.finlypal.util;

import com.derrick.finlypal.exception.NotAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${application.jwt.secret}")
    private String secret;
    @Value("${application.jwt.accessTokenExpiration}")
    private Long accessTokenExpiration;
    @Value("${application.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey generateSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenExpiration);
    }

    public String buildToken(UserDetails userDetails, Long expiration) {
        return buildToken(userDetails, new HashMap<>(), expiration);
    }

    public String buildToken(
            UserDetails userDetails,
            Map<String, Object> extraClaims,
            Long expiration

    ) {
        return Jwts
                .builder()
                .claims()
                .add(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(generateSecretKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public void validateUserAccess(String user) throws NotAuthorizedException {
        String authenticatedUsername = getAuthenticationUsername();

        if (!authenticatedUsername.equals(user)) {
            throw new NotAuthorizedException(
                    "Access denied: User " + authenticatedUsername + " is not authorized to perform this action"
            );
        }
    }

    private String getAuthenticationUsername() throws NotAuthorizedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String username) {
            return username;
        }

        throw new NotAuthorizedException("User authentication details are missing");
    }
}
