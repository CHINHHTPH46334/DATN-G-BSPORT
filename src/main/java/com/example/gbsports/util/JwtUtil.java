package com.example.gbsports.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    // Tiêm secretKeyString qua constructor
    public JwtUtil(@Value("${jwt.secret}") String secretKeyString) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyString));
    }

    // Tạo reset token
    public String generateResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("type", "RESET_TOKEN"); // Đánh dấu đây là reset token
        return createToken(claims, email, 60 * 60 * 1000); // 1 giờ (miliseconds)
    }

    // Tạo reset token
    public String generateResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("type", "RESET_TOKEN"); // Đánh dấu đây là reset token
        return createToken(claims, email, 60 * 60 * 1000); // 1 giờ (miliseconds)
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = (List<String>) claims.get("roles");
        return roles != null ? roles : List.of();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername(), 10 * 60 * 60 * 1000); // 10 hours
    }

    // Xác thực reset token và lấy email
    public String validateResetTokenAndGetEmail(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"RESET_TOKEN".equals(claims.get("type"))) {
                throw new IllegalArgumentException("Invalid token type");
            }
            if (isTokenExpired(token)) {
                throw new IllegalArgumentException("Token has expired");
            }
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token: " + e.getMessage());
        }
    }

    private String createToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SECRET_KEY) // Sử dụng SecretKey
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}