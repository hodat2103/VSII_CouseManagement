package com.vsii.coursemanagement.components;

import com.nimbusds.jwt.JWTClaimsSet;
import com.vsii.coursemanagement.configurations.TokenManager;
import com.vsii.coursemanagement.entities.Account;
import com.vsii.coursemanagement.exceptions.InvalidParamException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Autowired
    private TokenManager tokenManager;
    public String generateToken(Account account) throws Exception {
//        Map<String, Objects> claims = new HashMap<>();
////        this.generateSecretKey();
////        try {
//            String token = Jwts.builder()
//                    .setClaims(claims)
//                    .setSubject(account.getUsername()) // authen user
//                    .setIssuedAt(new Date(System.currentTimeMillis()))
//                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) //set expiration for token
//                    .signWith(getSecretInKey(), SignatureAlgorithm.HS256) //sign token bay secretKey & algorithm HS256
//                    .compact();// create token by String char
//            return token;
////        }catch (Exception e){
////            throw new InvalidParamException("Cannot create token because error: " + e.getMessage());
////        }
////        return null;
        JWTClaimsSet claimsInput = new JWTClaimsSet.Builder()
                .subject("account_info")
                .claim("phoneNumber", account.getPhoneNumber())
                .issueTime(new Date())                     // Time issue token.
                .issuer("https://your-app.com")            // App issue token.
                .expirationTime(new Date(System.currentTimeMillis() + expiration * 1000L)) // expiration token (1 hour).
                .build();
        String encryptedTokenString = tokenManager.toEncryptedToken(claimsInput);
        return  encryptedTokenString;
    }

    private Key getSecretInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("PstJjnP30Ohm2YKW/bgvGvk80UFeylLdzcbHcH136z4="));
        return Keys.hmacShaKeyFor(bytes);
    }
    private String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSecretInKey())
                .build()
                .parseClaimsJws(token)// decode token, check valid ?
                .getBody();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public boolean isTokenExpired(String token) throws Exception {
//        Date exprirationDate = this.extractClaims(token, Claims::getExpiration);
//        return exprirationDate.before(new Date());

        JWTClaimsSet claims = tokenManager.parseEncryptedToken(token);
        Date expirationTime = claims.getExpirationTime();
        return expirationTime != null && expirationTime.before(new Date());
    }

    public String extractUsername(String token) throws Exception {
//        return extractClaims(token, Claims::getSubject);

        JWTClaimsSet claims = tokenManager.parseEncryptedToken(token);
        return (String) claims.getClaim("phoneNumber");
    }

    //check username & expiration Token
    public boolean validateToken(String token, UserDetails userDetails) throws Exception {
        String phoneNumber = extractUsername(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

