package com.fof.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // Permet à Spring de gérer automatiquement cette classe comme un "service"
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long jwtExpirationMs = 1000 * 60 * 15; // 15 minutes

    private Key key;

    //Création de la clé HMAC
    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /** Génère un access token (durée courte) */
    public String generateAccessToken(String username, String role) {

        Date now = new Date(); // maintenant
        Date expiry = new Date(now.getTime() + jwtExpirationMs);// expire dans 15 minutes

        return Jwts.builder()
                .setSubject(username)   // sujet = username (email)
                .claim("role", role) //  rôle
                .setIssuedAt(now)       // date de créatio
                .setExpiration(expiry)  //date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) //signature avec une clé secrète
                .compact(); // génère le token;
    }

    /** Génère un refresh token (durée longue) */
    public String generateRefreshToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 jours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /** 🔎 Extrait le username (email) contenu dans un token */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)   // clé utilisée pour la signature
                .build()
                .parseClaimsJws(token)      // analyse du token
                .getBody()                  // récupère le contenu du token (les données, appelées claims)
                .getSubject();              // récupère le "subject" (username)
    }

    /** ✅ Vérifie si un token est valide */
    public boolean validateJwtToken(String token) {
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

    /** Lire le rôle depuis le token */
    public String getRoleFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}