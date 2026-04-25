package com.fof.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration principale de la sécurité de l'application.
 *
 * <p>
 * Cette classe configure Spring Security pour protéger l'API REST avec une
 * authentification stateless basée sur JWT.
 * </p>
 *
 * <p>
 * Responsabilités principales :
 * <ul>
 *     <li>Désactivation du CSRF pour une API REST stateless</li>
 *     <li>Configuration des règles CORS</li>
 *     <li>Définition des routes publiques et protégées</li>
 *     <li>Protection des endpoints selon les rôles utilisateur</li>
 *     <li>Ajout du filtre JWT dans la chaîne de sécurité</li>
 *     <li>Exposition de l'AuthenticationManager</li>
 * </ul>
 * </p>
 *
 * <p>
 * Cette configuration est adaptée à une architecture frontend/backend séparée,
 * Angular pour le frontend et Spring Boot pour l'API.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filtre chargé d'intercepter les requêtes HTTP et de valider les tokens JWT.
     */
    private final JwtAuthFilter jwtAuthenticationFilter;

    /**
     * Liste des origines frontend autorisées à appeler l'API.
     *
     * <p>
     * La valeur est externalisée afin de permettre une configuration différente
     * entre l'environnement local, Render et de futurs environnements SaaS.
     * Plusieurs origines peuvent être séparées par des virgules.
     * </p>
     */
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Injecte le filtre JWT utilisé par la chaîne de sécurité Spring.
     *
     * @param jwtAuthenticationFilter filtre d'authentification JWT
     */
    public SecurityConfig(JwtAuthFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configure la chaîne principale de sécurité HTTP.
     *
     * <p>
     * L'application fonctionne en mode stateless :
     * aucune session serveur n'est créée, et chaque requête protégée
     * doit fournir un token JWT valide.
     * </p>
     *
     * <p>
     * Règles principales :
     * <ul>
     *     <li>Les routes d'authentification et Swagger sont publiques</li>
     *     <li>Les requêtes OPTIONS sont autorisées pour le CORS</li>
     *     <li>Les routes IA et admin sont réservées aux administrateurs</li>
     *     <li>Les routes de ventes nécessitent une authentification</li>
     *     <li>Les routes utilisateur sont accessibles aux USER et ADMIN</li>
     * </ul>
     * </p>
     *
     * @param http objet de configuration Spring Security
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception si la configuration de sécurité échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                // Active la configuration CORS pour les appels frontend/backend séparés
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth

                        // Routes publiques nécessaires à l'authentification et à la documentation API
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Autorise les requêtes preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Routes protégées selon les rôles
                        .requestMatchers("/api/ai/**").hasRole("ADMIN")
                        .requestMatchers("/api/sales/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")

                        /*
                         * Sécurise toutes les autres routes non explicitement définies.
                         *
                         * Cela évite qu’un endpoint ajouté plus tard soit exposé publiquement par erreur.
                         * Approche recommandée en production et en SaaS (principe du "secure by default").
                         */
                        .anyRequest().authenticated()
                )

                // Désactive les sessions serveur pour conserver une API REST stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Ajoute le filtre JWT avant le filtre standard d'authentification Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Définit les règles CORS utilisées par l'application.
     *
     * <p>
     * Les origines autorisées sont lues depuis la configuration
     * {@code cors.allowed-origins}. Cela permet d'autoriser plusieurs frontends :
     * local, staging, production ou futurs tenants.
     * </p>
     *
     * @return une source de configuration CORS utilisée par Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();

        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Expose l'AuthenticationManager fourni par Spring Security.
     *
     * <p>
     * Ce bean peut être utilisé par les services d'authentification
     * pour vérifier les identifiants utilisateur.
     * </p>
     *
     * @param config configuration d'authentification Spring Security
     * @return l'AuthenticationManager configuré par Spring
     * @throws Exception si la récupération de l'AuthenticationManager échoue
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}