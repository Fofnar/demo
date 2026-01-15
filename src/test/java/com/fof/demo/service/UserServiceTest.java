package com.fof.demo.service;

import com.fof.demo.entity.AppUser;
import com.fof.demo.model.User;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.repository.UserRepository;
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
        AppUser fakeUser = new AppUser(1L, "fof", "pwd123", "USER");

        // Simuler : si on appelle userRepository.findByUsername(...) → renvoyer fakeUser
        when(appUserRepository.findByUsername("fof")).thenReturn(Optional.of(fakeUser));

        // ACT : appeler le service
        AppUser result = appUserService.loadUserByUsername("fof");

        // Assert: verifier les resultats
        assertNotNull(result); // l'utilisateur doit exister
        assertEquals("fof", result.getUsername());

    }

    @Test
    void testFindByUsername_NotFound(){
        // Arrange: aucun utilisateur
        when(appUserRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        //ACT
        AppUser result = appUserService.loadUserByUsername("ghost");

        //ASSERT
        assertNull(result);

    }

    @Test
    void testSaveAppUser(){
        AppUser newUser = new AppUser(null, "newuser", "pwd123", "USER");
        when(appUserRepository.save(newUser)).thenReturn(newUser);

        AppUser saved = appUserService.saveUser("newuser", "pwd123");

        //ASSERT
        assertNotNull(saved);
        assertEquals("newuser", saved.getUsername());

        // Vérifier que userRepository.save a bien été appelé UNE fois
        verify(appUserRepository, times(1)).save(newUser);
    }
}
