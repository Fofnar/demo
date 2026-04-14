package com.fof.demo;

import com.fof.demo.entity.AppUser;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.service.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(AppUserService userService, AppUserRepository userRepository) {
		return args -> {

			// Créer admin seulement si AUCUN user existe
			if (userRepository.count() == 0) {

				AppUser admin = userService.saveUser(
						"fofanafodeba411@gmail.com",
						"Fofana",
						"Fodeba",
						20,
						"Fodeba123",
						"0600000000"
				);

				// 🔥 Promotion via méthode dédiée
				userService.promoteToAdmin(admin);

				System.out.println("✅ Admin créé automatiquement");
			} else {
				System.out.println("ℹ️ Users déjà présents, skip init");
			}
		};
	}
}