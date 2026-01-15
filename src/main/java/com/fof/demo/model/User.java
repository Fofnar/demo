package com.fof.demo.model;

import jakarta.persistence.*;

@Entity // Dit à Hibernate : “cette classe mappe une table”
@Table(name = "users")    // ← ici on évite le mot réservé USER
public class User {


    @Id // Clé primaire de la table
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // GenerationType.IDENTITY → la base incrémente automatiquement l’ID
    private  Long id;

    private String name;
    private int age;

    public User(){} //JPA appelle ce constructeur pour créer l’objet

    public User(String name, int age){
        this.name = name;
        this.age = age;
    }

    // Getters & setters pour que Hibernate puisse lire/écrire les champs
    public Long getId(){ return id;}
    public void setId(long id){ this.id = id;}

    public String getName(){ return name;}

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() { return age; }

    public void setAge(int age){ this.age = age; }
}
