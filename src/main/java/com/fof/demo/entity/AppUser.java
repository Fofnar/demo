package com.fof.demo.entity;


import  lombok.*;
import jakarta.persistence.*;

@Entity  //  Indique que cette classe est une table dans la base de données
@Table(name = "app_user") // Ici on évite le mot réservé USER
@Data    //  Lombok : génère automatiquement les getters, setters, toString, equals, hashCode
@NoArgsConstructor  //  Lombok : génère un constructeur sans argument
@AllArgsConstructor //  Lombok : génère un constructeur avec tous les arguments
@Builder            // Lombok : permet d'utiliser le design pattern "Builder"
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER";  // ==> Par défaut, tous les utilisateurs auront le rôle USER

}
