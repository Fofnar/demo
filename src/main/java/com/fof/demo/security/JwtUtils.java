package com.fof.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // Permet à Spring de gérer automatiquement cette classe comme un "service"
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long jwtExpirationMs = 86400000; // 24h

    /** 🔐 Génère un token JWT */
    public String generateJwtToken(String username) {
        Date now = new Date(); // maintenant
        Date expiry = new Date(now.getTime() + jwtExpirationMs);// expire dans 24h

        //transfome la string en clé HMAC
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)   // sujet = username
                .setIssuedAt(now)       // date de création
                .setExpiration(expiry)  //date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) //signature avec une clé secrète
                .compact(); // génère le token;
    }

    /** 🔎 Extrait le username contenu dans un token */
    public String getUserNameFromJwtToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)   // clé utilisée pour la signature
                .build()
                .parseClaimsJws(token)      // analyse du token
                .getBody()                  // récupère le contenu du token (les données, appelées claims)
                .getSubject();              // récupère le "subject" (username)
    }

    /** ✅ Vérifie si un token est valide */
    public boolean validateJwtToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true; // token valide
        } catch (Exception e) {
            //token expiré, signature invalide, mal formé, etc.
            return false;
        }
    }

}
