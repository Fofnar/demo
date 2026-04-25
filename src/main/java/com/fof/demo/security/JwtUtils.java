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

/**
 * Classe utilitaire responsable de la génération, lecture et validation des tokens JWT.
 *
 * <p>
 * Elle centralise toute la logique liée à l'authentification stateless :
 * <ul>
 *     <li>Génération d'access tokens à durée courte</li>
 *     <li>Génération de refresh tokens à durée longue</li>
 *     <li>Extraction du username depuis un token</li>
 *     <li>Extraction du rôle utilisateur</li>
 *     <li>Validation de la signature et de l'expiration du token</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les tokens sont signés avec l'algorithme HS256 à partir d'une clé secrète
 * fournie par la configuration applicative ({@code jwt.secret}).
 * </p>
 *
 * <p>
 * Cette classe est utilisée par le système de sécurité Spring Security
 * pour protéger les endpoints de l'API REST.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Component
public class JwtUtils {

    /**
     * Clé secrète utilisée pour signer et vérifier les tokens JWT.
     *
     * <p>
     * Cette valeur doit être fournie via une variable d'environnement
     * ou un fichier de configuration sécurisé.
     * </p>
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Durée de validité de l'access token.
     *
     * <p>
     * Ici, l'access token expire après 15 minutes.
     * Cette durée courte limite les risques en cas de vol du token.
     * </p>
     */
    private final long jwtExpirationMs = 1000 * 60 * 15;

    /**
     * Durée de validité du refresh token.
     *
     * <p>
     * Ici, le refresh token expire après 7 jours.
     * Cette durée offre un bon équilibre entre sécurité et expérience utilisateur.
     * </p>
     */
    private final long refreshExpirationMs = 1000L * 60 * 60 * 24 * 7;

    /**
     * Clé HMAC générée à partir du secret JWT.
     */
    private Key key;

    /**
     * Initialise la clé de signature JWT après l'injection des dépendances Spring.
     *
     * <p>
     * La clé est construite à partir du secret configuré dans {@code jwt.secret}.
     * Elle sera utilisée pour signer et valider les tokens.
     * </p>
     */
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un access token JWT à durée courte.
     *
     * <p>
     * L'access token contient :
     * <ul>
     *     <li>Le username de l'utilisateur comme subject</li>
     *     <li>Le rôle de l'utilisateur comme claim</li>
     *     <li>Une date de création</li>
     *     <li>Une date d'expiration courte</li>
     * </ul>
     * </p>
     *
     * @param username email ou identifiant principal de l'utilisateur
     * @param role rôle de l'utilisateur
     * @return un access token JWT signé
     */
    public String generateAccessToken(String username, String role) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Génère un refresh token JWT à durée longue.
     *
     * <p>
     * Le refresh token permet de renouveler un access token expiré
     * sans obliger l'utilisateur à se reconnecter immédiatement.
     * </p>
     *
     * @param username email ou identifiant principal de l'utilisateur
     * @return un refresh token JWT signé
     */
    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le username contenu dans un token JWT.
     *
     * <p>
     * Dans cette application, le username correspond généralement à l'email utilisateur.
     * </p>
     *
     * @param token token JWT à analyser
     * @return le username contenu dans le subject du token
     */
    public String getUserNameFromJwtToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Vérifie si un token JWT est valide.
     *
     * <p>
     * La validation vérifie notamment :
     * <ul>
     *     <li>La signature du token</li>
     *     <li>Le format du token</li>
     *     <li>La date d'expiration</li>
     * </ul>
     * </p>
     *
     * @param token token JWT à valider
     * @return {@code true} si le token est valide, sinon {@code false}
     */
    public boolean validateJwtToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait le rôle utilisateur depuis un token JWT.
     *
     * <p>
     * Cette méthode lit le claim {@code role} stocké dans le token.
     * Elle peut être utilisée pour reconstruire les autorisations côté backend.
     * </p>
     *
     * @param token token JWT à analyser
     * @return le rôle contenu dans le token
     */
    public String getRoleFromJwtToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}