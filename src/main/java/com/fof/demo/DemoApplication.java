package com.fof.demo;

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
	public CommandLineRunner run(AppUserService userService) {
		return args -> {
			// Cette ligne s'exécutera au démarrage de l'appli
			userService.saveUser(
					"fofnar@gmail.com",
					"Fofana",
					"Fodeba",
					20,
					"Fodeba123",
					"0600000000"
			);
		};
	}
}
