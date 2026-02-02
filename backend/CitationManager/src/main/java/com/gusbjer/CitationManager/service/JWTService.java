package com.gusbjer.CitationManager.service;

import com.gusbjer.CitationManager.dto.AuthTokenWrapperDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private final String secretAccessKey;
    private final String secretRefreshKey;

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey skAccess = keyGen.generateKey();
            secretAccessKey = Base64.getEncoder().encodeToString(skAccess.getEncoded());

            SecretKey skRefresh = keyGen.generateKey();
            secretRefreshKey = Base64.getEncoder().encodeToString(skRefresh.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthTokenWrapperDto generateAuthToken(String username) {
        return new AuthTokenWrapperDto(
                generateAccessToken(username),
                generateRefreshToken(username));
    }

    public String generateAccessToken(String username) {
        return generateToken(username, Duration.ofSeconds(30), secretAccessKey);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, Duration.ofHours(1), secretRefreshKey);
    }

    public String generateToken(String username, Duration duration, String secretKey) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + duration.toMillis()))
                .signWith(getKey(secretKey))
                .compact();
    }

    private Key getKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public String extractUserName(String token, boolean useAccessKey) {
        String secretKey = useAccessKey ? secretAccessKey : secretRefreshKey;
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(secretKey))
                .build()
                .parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails, boolean useAccessKey) {
        final String secretKey = useAccessKey ? secretAccessKey : secretRefreshKey;
        final String username = extractUserName(token, secretKey);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey);
    }

    public boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public boolean isTokenExpired(String token, boolean useAccessKey) {
        String secretKey = useAccessKey ? secretAccessKey : secretRefreshKey;
        return extractExpiration(token, secretKey).before(new Date());
    }

    private Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }
}
