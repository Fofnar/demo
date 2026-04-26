package com.fof.demo;

import com.fof.demo.entity.AppUser;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.service.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Point d'entrée principal de l'application Spring Boot.
 *
 * <p>
 * Cette classe démarre l'application et configure les initialisations nécessaires
 * au lancement, notamment la création automatique d'un administrateur par défaut.
 * </p>
 *
 * <p>
 * Fonctionnalité clé :
 * <ul>
 *     <li>Création d’un utilisateur ADMIN uniquement si la base de données est vide</li>
 *     <li>Permet une initialisation automatique en environnement de développement ou déploiement initial</li>
 * </ul>
 * </p>
 *
 * <p>
 * ⚠️ En environnement de production avancé (multi-tenant ou SaaS),
 * ce mécanisme devra être remplacé par un système d’onboarding sécurisé
 * ou une gestion centralisée des administrateurs.
 * </p>
 *
 * @author Fodeba Fofana
 * @project Felyxor — AI-Powered Business Intelligence Platform
 * @version 1.0
 */
@SpringBootApplication
public class DemoApplication {

	/**
	 * Méthode principale lançant l'application Spring Boot.
	 *
	 * @param args arguments de démarrage
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 * Initialisation automatique au démarrage de l'application.
	 *
	 * <p>
	 * Ce runner vérifie si aucun utilisateur n'existe en base de données.
	 * Si c'est le cas, il crée un utilisateur administrateur par défaut
	 * puis le promeut en ADMIN.
	 * </p>
	 *
	 * <p>
	 * Objectif :
	 * éviter d’avoir une application sans accès administrateur lors du premier lancement.
	 * </p>
	 *
	 * @param userService service de gestion des utilisateurs
	 * @param userRepository repository des utilisateurs
	 * @return un {@link CommandLineRunner} exécuté au démarrage
	 */
	@Bean
	public CommandLineRunner run(AppUserService userService, AppUserRepository userRepository) {
		return args -> {

			// Création d'un admin uniquement si aucun utilisateur n'existe
			if (userRepository.count() == 0) {

				AppUser admin = userService.saveUser(
						"fofanafodeba411@gmail.com",
						"Fofana",
						"Fodeba",
						20,
						"Fodeba123",
						"0600000000"
				);

				// Promotion via méthode dédiée
				userService.promoteToAdmin(admin);

				System.out.println("✅ Admin created automatically");
			} else {
				System.out.println("ℹ️ Utilisateurs déjà présents, initialisation ignorée");
			}
		};
	}
}