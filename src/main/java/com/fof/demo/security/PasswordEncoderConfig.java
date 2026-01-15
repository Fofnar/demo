package com.fof.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration  // ==> Déclare une classe de configuration Spring
public class PasswordEncoderConfig {

    @Bean // ==> Rend ce PasswordEncoder accessible partout
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
