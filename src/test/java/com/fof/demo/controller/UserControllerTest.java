package com.fof.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration du contrôleur utilisateur.
 *
 * <p>
 * Cette classe vérifie le bon fonctionnement des opérations principales
 * exposées par l'API utilisateur : création, récupération, mise à jour
 * et suppression.
 * </p>
 *
 * <p>
 * Les tests utilisent {@link MockMvc} afin de simuler des requêtes HTTP
 * sans lancer de serveur réel.
 * </p>
 *
 * @author Fodeba Fofana
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Vérifie qu'un utilisateur peut être créé avec succès.
     */
    @Test
    void testRegisterUser_Succes() throws Exception {

        UserDTO user = new UserDTO(
                null,
                "alpha@gmail.com",
                "Diallo",
                "Alpha",
                30,
                "060606060000",
                Role.USER
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alpha@gmail.com"))
                .andExpect(jsonPath("$.lastName").value("Diallo"))
                .andExpect(jsonPath("$.firstName").value("Alpha"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.phone").value("060606060000"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * Vérifie que la liste des utilisateurs est retournée au format JSON.
     */
    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Vérifie qu'un utilisateur existant peut être mis à jour.
     */
    @Test
    void testUpdateUser() throws Exception {

        UserDTO user = new UserDTO(
                null,
                "beta@gmail.com",
                "Diallo",
                "Beta",
                30,
                "0606060120",
                Role.USER
        );

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO createdUser = objectMapper.readValue(response, UserDTO.class);
        createdUser.setEmail("betaUpdated@gmail.com");

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("betaUpdated@gmail.com"));
    }

    /**
     * Vérifie qu'un utilisateur existant peut être supprimé.
     */
    @Test
    void testDeleteUser() throws Exception {

        UserDTO user = new UserDTO(
                null,
                "gamma@gmail.com",
                "Diallo",
                "Gamma",
                25,
                "06060600300",
                Role.USER
        );

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO createdUser = objectMapper.readValue(response, UserDTO.class);

        mockMvc.perform(delete("/api/users/" + createdUser.getId()))
                .andExpect(status().isOk());
    }
}