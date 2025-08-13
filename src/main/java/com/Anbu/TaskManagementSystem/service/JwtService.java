package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt_key_2}")
    private String secretKey;

    public String generateToken(Employee currentUser) {

        Map<String, String> customClaims = new HashMap<>();
        customClaims.putIfAbsent("ROLE",currentUser.getRole().name());

        return Jwts.builder()
                .claims()
                .add(customClaims)
                .subject(currentUser.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 12*60*60*1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims allClaims = extractClaims(token);
        return claimResolver.apply(allClaims);
    }

    public boolean isTokenValid(String token, Employee employee){
        String currentUserEmail = extractEmail(token);
        return currentUserEmail.equalsIgnoreCase(employee.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date tokenExpiration = extractClaim(token, Claims::getExpiration);
        return tokenExpiration.before(new Date());
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}