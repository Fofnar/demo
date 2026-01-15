package com.fof.demo.controller;
//Cette classe sert à tester le CRUD basique (name, age).


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fof.demo.model.User;
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
        User user = new User("Alpha", 30);

        mockMvc. perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON) //précise qu’on envoie du JSON
                        .content(objectMapper.writeValueAsString(user))) //met le corps de la requête
                .andExpect(status().isOk()) //Code 200 OK
                .andExpect(jsonPath("$.name").value("Alpha")) //JSON renvoyé contient bien name = Alpha
                .andExpect(jsonPath("$.age").value(30));

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
        User user = new User("Beta", 22);
        String userJson = objectMapper.writeValueAsString(user);

        // POST → crée un user
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extraire l'ID du user créé
        User createdUser = objectMapper.readValue(response, User.class);

        // ACT : mettre à jour son nom
        createdUser.setName("BetaUpdated");

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BetaUpdated"));
    }
    @Test
    void testDeleteUser() throws Exception {
        // On crée un user d’abord
        User user = new User("Gamma", 25);
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andReturn().getResponse().getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);

        // ACT : suppression
        mockMvc.perform(delete("/api/users/" + createdUser.getId()))
                .andExpect(status().isOk());
    }
}
