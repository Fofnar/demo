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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthenticationFilter;

    /*
     * Liste des origines frontend autorisées à appeler l'API.
     * La valeur est externalisée pour permettre une configuration différente
     * entre le développement local et l'environnement de production.
     */
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
     * Configure la sécurité globale de l'API.
     *
     * L'application utilise une architecture stateless basée sur JWT :
     * aucune session serveur n'est conservée, chaque requête protégée
     * doit fournir un token valide.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * Active la configuration CORS pour autoriser le frontend Angular
                 * à communiquer avec le backend depuis une origine différente.
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Routes publiques nécessaires à l'authentification
                         * et à la documentation API.
                         */
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        /*
                         * Autorise les requêtes preflight envoyées automatiquement
                         * par le navigateur avant certains appels cross-origin.
                         */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        /*
                         * Routes protégées selon le rôle ou l'authentification.
                         */
                        .requestMatchers("/api/ai/**").hasRole("ADMIN")
                        .requestMatchers("/api/sales/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")

                        /*
                         * Les autres routes restent publiques pour éviter de bloquer
                         * des endpoints non sensibles ou futurs endpoints publics.
                         */
                        .anyRequest().permitAll()
                )

                /*
                 * Désactive les sessions serveur pour garder une API REST stateless.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /*
                 * Ajoute le filtre JWT avant le filtre standard d'authentification Spring.
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /*
     * Définit les règles CORS utilisées par Spring Security.
     *
     * Les origines sont lues depuis la configuration afin de supporter :
     * - le frontend local Angular ;
     * - le futur frontend déployé sur Render ;
     * - plusieurs origines séparées par des virgules si nécessaire.
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

    /*
     * Expose l'AuthenticationManager fourni par Spring Security.
     * Il pourra être utilisé par les services d'authentification de l'application.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}