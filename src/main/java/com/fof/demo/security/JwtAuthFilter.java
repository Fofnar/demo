package com.fof.demo.security;

import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import com.fof.demo.service.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT exécuté une seule fois par requête.
 *
 * Rôle :
 * - ignorer les routes publiques
 * - lire le header Authorization
 * - valider le token JWT
 * - charger l'utilisateur depuis la base
 * - poser l'authentification dans le SecurityContext
 *
 *
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtUtils jwtUtils;
    private final AppUserService userService;

    /**
     * Indique au filtre quelles routes doivent être ignorées.
     *
     * Ici, on laisse passer :
     * - les endpoints d'authentification
     * - Swagger
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    /**
     * Logique principale du filtre JWT.
     *
     * Si un token valide est présent, l'utilisateur est authentifié
     * dans le contexte Spring Security.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtils.validateJwtToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AppUser user = userService.loadUserByUsername(username);

            if (user != null) {
                Role role = user.getRole();
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}