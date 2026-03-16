package com.fof.demo.entity;


import com.fof.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import  lombok.*;
import jakarta.persistence.*;

@Entity  //  Indique que cette classe est une table dans la base de données
@Table(name = "app_user")
@Data    //  Lombok : génère automatiquement les getters, setters, toString, equals, hashCode
@NoArgsConstructor  //  Lombok : génère un constructeur sans argument
@AllArgsConstructor //  Lombok : génère un constructeur avec tous les arguments
@Builder            // Lombok : permet d'utiliser le design pattern "Builder"
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private  String firstName;

    @Min(0)
    private int age;

    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @Column(unique = true, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

}
