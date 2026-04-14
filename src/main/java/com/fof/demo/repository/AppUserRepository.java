package com.fof.demo.repository;

import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserRepository extends
        JpaRepository<AppUser, Long>,
        JpaSpecificationExecutor<AppUser> //activer Specification
{

    // Permet de récupérer un utilisateur par son email (utilisé pour le login)
    Optional<AppUser> findByEmail(String email);

    //Permet de récupérer un utilisateur par son role (Filtering)
    Page<AppUser> findByRole(Role role, Pageable pageable);

    //Permet de récupérer un utilisateur par son numero de telephone
    Optional<AppUser> findByPhone(String phone);

}
