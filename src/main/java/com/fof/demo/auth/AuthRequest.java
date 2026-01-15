package com.fof.demo.auth;

public class AuthRequest {
    private String username;
    private String password;

    // Constructeur vide requis pour que Spring puisse instancier l'objet
    public AuthRequest() {}

    // Constructeur utile si on veut créer l'objet nous-même
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters (accès aux champs)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters (modification des champs)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
