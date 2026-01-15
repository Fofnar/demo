package com.fof.demo.repository;

import com.fof.demo.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long>{
    Optional<AppUser> findByUsername(String username);
    // ==> Permet de récupérer un utilisateur par son nom d'utilisateur (utilisé pour le login)
}
