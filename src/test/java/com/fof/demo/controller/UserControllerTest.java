package com.fof.demo.controller;
//Cette classe sert à tester le CRUD basique (name, age).


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

@SpringBootTest // On lance le vrai contexte Spring (application)
@AutoConfigureMockMvc // On dit à Spring de configurer MockMvc automatiquement
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Outil pour envoyer des requêtes HTTP simulées

    @Autowired
    private ObjectMapper objectMapper; // Pour transformer un objet Java en JSON

    @Test
    void testRegisterUser_Succes() throws Exception {

        //Créer un utilisateur à enregistrer
        UserDTO user = new UserDTO(null,
                "alpha@gmail.com",
                "Diallo",
                "Alpha",
                30,
                "060606060000",
                Role.USER
        );

        mockMvc. perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON) //précise qu’on envoie du JSON
                        .content(objectMapper.writeValueAsString(user))) //met le corps de la requête
                .andExpect(status().isOk()) //Code 200 OK
                .andExpect(jsonPath("$.email").value("alpha@gmail.com")) //JSON renvoyé contient bien email = alpha@gmail.com
                .andExpect(jsonPath("$.lastName").value("Diallo"))
                .andExpect(jsonPath("$.firstName").value("Alpha"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.phone").value("060606060000"))
                .andExpect(jsonPath("$.role").value("USER"));


    }

    @Test
    //Vérifier que l’endpoint GET /api/users renvoie bien du JSON et un code 200
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void testUpdateUser() throws Exception {
        // On crée un user d’abord
        UserDTO user = new UserDTO(null,
                "beta@gmail.com",
                "Diallo",
                "Beta",
                30,
                "0606060120",
                Role.USER
        );
        String userJson = objectMapper.writeValueAsString(user);

        // POST → crée un user
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extraire l'ID du user créé
        UserDTO createdUser = objectMapper.readValue(response, UserDTO.class);

        // ACT : mettre à jour son nom
        createdUser.setEmail("betaUpdated@gmail.com");

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("betaUpdated@gmail.com"));
    }
    @Test
    void testDeleteUser() throws Exception {
        // On crée un user d’abord
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
                .andReturn().getResponse().getContentAsString();

        UserDTO createdUser = objectMapper.readValue(response, UserDTO.class);

        // ACT : suppression
        mockMvc.perform(delete("/api/users/" + createdUser.getId()))
                .andExpect(status().isOk());
    }
}
