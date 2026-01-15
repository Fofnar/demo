package com.fof.demo.security;

import com.fof.demo.entity.AppUser;                               // Notre entité utilisateur
import com.fof.demo.service.AppUserService;                       // Service pour charger un user par username
import jakarta.servlet.FilterChain;                               // Chaîne de filtres (middleware)
import jakarta.servlet.ServletException;                          // Exception servlet
import jakarta.servlet.http.HttpServletRequest;                   // Requête HTTP entrante
import jakarta.servlet.http.HttpServletResponse;                  // Réponse HTTP sortante
import lombok.RequiredArgsConstructor;                            // Génère un constructeur pour les champs 'final'
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Représente un utilisateur authentifié
import org.springframework.security.core.authority.SimpleGrantedAuthority;             // Représente une autorité/role
import org.springframework.security.core.context.SecurityContextHolder;                // Contexte de sécurité courant
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Détails web de l'auth
import org.springframework.stereotype.Component;                  // Rend la classe détectable par Spring
import org.springframework.web.filter.OncePerRequestFilter;       // Filtre exécuté une fois par requête

import java.io.IOException;                                       // Exception IO
import java.util.List;                                            // Pour créer la liste d’autorisations

@Component                              // ➜ Spring gère ce bean et peut l’injecter
@RequiredArgsConstructor                 // ➜ constructeur pour les 'final' (jwtUtils, userService)
public class JwtAuthFilter extends OncePerRequestFilter {  // ➜ Filtre qui s’exécute à CHAQUE requête

    private final JwtUtils jwtUtils;                // ➜ outil pour générer/valider/lire le JWT
    private final AppUserService userService;       // ➜ pour charger l’utilisateur depuis la DB

    @Override
    protected void doFilterInternal(                // ➜ point d’entrée du filtre
                                                    HttpServletRequest request,             // ➜ requête entrante
                                                    HttpServletResponse response,           // ➜ réponse sortante
                                                    FilterChain filterChain                 // ➜ permet de chaîner au filtre suivant
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // ➜ récupère le header "Authorization"

        // ➜ Si pas de header, ou ne commence pas par "Bearer ", on laisse passer sans authentifier
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);            // ➜ on continue la chaîne
            return;                                             // ➜ et on sort du filtre
        }

        String token = authHeader.substring(7);                 // ➜ enlève "Bearer " (7 caractères)
        // ➜ On vérifie d’abord que le token est bien formé, signé et pas expiré
        if (!jwtUtils.validateJwtToken(token)) {
            filterChain.doFilter(request, response);            // ➜ token invalide : pas d’authent
            return;
        }

        // ➜ Récupère le username contenu dans le token ("sub")
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // ➜ Si déjà authentifié sur ce thread, inutile de refaire
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ➜ On charge l’utilisateur en DB (pour récupérer par ex. son rôle)
            AppUser user = userService.loadUserByUsername(username);
            if (user != null) {
                // ➜ Prépare la liste d’autorisations (Spring attend "ROLE_...")
                String role = user.getRole();                                // ex: "USER" ou "ROLE_USER"
                String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(springRole));

                // ➜ Crée un "utilisateur authentifié" pour Spring (pas besoin du mot de passe ici)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // ➜ Ajoute quelques détails utiles (adresse IP, session, etc.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ➜ Pose l’authentification dans le contexte de sécurité (à partir de maintenant, "on est connecté")
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //  on continue la chaîne des filtres (sinon la requête n’atteint jamais le contrôleur)
        filterChain.doFilter(request, response);
    }
}
