package com.infinitetechies.ecom.service.inf;

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

public interface IJwtService {
    public String generateToken(String email) throws NoSuchAlgorithmException;
    public Key getKey() throws NoSuchAlgorithmException;
    public boolean validateToken(String token, UserDetails userDetails);
    public String extractUserEmail(String token);
}
