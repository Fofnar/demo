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

/**
 * Tests unitaires du service {@link AppUserService}.
 *
 * <p>
 * Cette classe vérifie le comportement du service utilisateur en isolant
 * la couche repository grâce à Mockito.
 * </p>
 *
 * <p>
 * Objectifs :
 * <ul>
 *     <li>Tester la récupération d'un utilisateur par email</li>
 *     <li>Vérifier le comportement en cas d'utilisateur absent</li>
 *     <li>Tester la création d'un utilisateur</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ces tests n'utilisent pas de base de données réelle :
 * le repository est simulé pour garantir des tests rapides et isolés.
 * </p>
 *
 * @author Fodeba Fofana
 */
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    /**
     * Initialise les mocks avant chaque test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Vérifie qu'un utilisateur existant est correctement récupéré par email.
     */
    @Test
    void testFindByUsername_Found() {

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

        when(appUserRepository.findByEmail("fof123@gmail.com"))
                .thenReturn(Optional.of(fakeUser));

        AppUser result = appUserService.loadUserByUsername("fof123@gmail.com");

        assertNotNull(result);
        assertEquals("fof123@gmail.com", result.getEmail());
    }

    /**
     * Vérifie que null est retourné si aucun utilisateur n'est trouvé.
     */
    @Test
    void testFindByUsername_NotFound() {

        when(appUserRepository.findByEmail("ghost@gmail.com"))
                .thenReturn(Optional.empty());

        AppUser result = appUserService.loadUserByUsername("ghost@gmail.com");

        assertNull(result);
    }

    /**
     * Vérifie qu'un utilisateur est correctement enregistré.
     */
    @Test
    void testSaveAppUser() {

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

        when(appUserRepository.save(any(AppUser.class)))
                .thenReturn(newUser);

        AppUser saved = appUserService.saveUser(
                "newuser@gmail.com",
                "Lnewuser",
                "Fnewuser",
                25,
                "pwd12345",
                "070101010000"
        );

        assertNotNull(saved);
        assertEquals("newuser@gmail.com", saved.getEmail());

        verify(appUserRepository, times(1)).save(any(AppUser.class));
    }
}