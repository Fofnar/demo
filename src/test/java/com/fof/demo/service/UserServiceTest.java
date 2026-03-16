package com.fof.demo.service;

import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import com.fof.demo.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock // Permet de créer un faux objet
    private AppUserRepository appUserRepository; // Faux repository qui ne se connecte pas à une vraie DB

    @InjectMocks //Demande à Mockito d'injecter les mocks dans notre service
    private AppUserService appUserService; // Service qu’on teste (il reçoit le faux userRepository)

    @BeforeEach
    void setUp(){
        // Initialise les annotations @Mock et @InjectMocks avant chaque test
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testFindByUsername_Found(){

        // ARRANGE : créer un faux utilisateur
        AppUser fakeUser = new AppUser(
                1L,
                "fof123@gmail.com",
                "Fof",
                "Fofnar",
                20,
                "Fofnar12345",
                "0700001000000",
                Role.USER
        );

        // Simuler : si on appelle userRepository.findByUsername(...) → renvoyer fakeUser
        when(appUserRepository.findByEmail("fof123@gmail.com")).thenReturn(Optional.of(fakeUser));

        // ACT : appeler le service
        AppUser result = appUserService.loadUserByUsername("fof123@gmail.com");

        // Assert: verifier les resultats
        assertNotNull(result); // l'utilisateur doit exister
        assertEquals("fof123@gmail.com", result.getEmail());

    }

    @Test
    void testFindByUsername_NotFound(){
        // Arrange: aucun utilisateur
        when(appUserRepository.findByEmail("ghost@gmail.com")).thenReturn(Optional.empty());

        //ACT
        AppUser result = appUserService.loadUserByUsername("ghost@gmail.com");

        //ASSERT
        assertNull(result);

    }

    @Test
    void testSaveAppUser(){

        AppUser newUser = new AppUser(
                null,
                "newuser@gmail.com",
                "Lnewuser",
                "Fnewuser",
                25,
                "pwd12345",
                "070101010000",
                Role.USER
        );
        when(appUserRepository.save(any(AppUser.class))).thenReturn(newUser);

        AppUser saved = appUserService.saveUser(
                "newuser@gmail.com",
                "Lnewuser",
                "Fnewuser",
                25,
                "pwd12345",
                "070101010000"
        );

        //ASSERT
        assertNotNull(saved);
        assertEquals("newuser@gmail.com", saved.getEmail());

        // Vérifier que userRepository.save a bien été appelé UNE fois
        verify(appUserRepository, times(1)).save(any(AppUser.class));
    }
}
